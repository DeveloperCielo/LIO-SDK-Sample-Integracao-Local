# Integração Local

## Apresentação

O objetivo da Integração Local da Cielo LIO é permitir que o aplicativo desenvolvido em Android se integre com o módulo de pedidos e pagamentos da Cielo LIO através do Cielo LIO Order Manager SDK.

Nesse modelo de integração, toda a gestão do estabelecimento comercial e da arquitetura da solução fica sob responsabilidade do aplicativo que irá utilizar o SDK nas operações de pagamento.

- O aplicativo do parceiro rodando na Cielo envia informações para o Cielo LIO Order Manager SDK.
- O Cielo LIO Order Manager SDK executa os fluxos para pagamento.
- O aplicativo do parceiro recebe as informações do pagamento e continua sua execução.

As seções abaixo possuem todas as informações necessárias para realizar a integração de forma rápida e segura.

## Cielo Lio Order Manager SDK

### Sobre

O Cielo LIO Order Manager SDK é uma biblioteca Android desenvolvida com base na Cielo LIO Order Manager API, encapsulando a complexidade da comunicação REST em uma API fluente e amigável ao desenvolvedor e permitindo uma integração simples, rápida e segura com a plataforma Cielo LIO.

O principal objetivo do Cielo LIO Order Manager SDK é simplificar a integração com o sistema de pagamentos e permitir que o desenvolvedor concentre esforços no desenvolvimento das características do seu aplicativo. Abaixo, é apresentado um exemplo em alto nível do processo de utilização do Cielo LIO Order Manager SDK por um aplicativo parceiro:

![fluxo](https://desenvolvedores.cielo.com.br/api-portal/sites/default/files/sdk-order.jpg)

1. O aplicativo do parceiro é responsável por gerenciar todas as informações do pedido e, então, enviá-las para o Cielo LIO Order Manager SDK.
2. O Cielo LIO Order Manager SDK recebe as informações e o fluxo de pagamento para o cliente. Neste fluxo, o cliente irá selecionar a forma de pagamento e digitar a senha no Pinpad, tendo a possibilidade de enviar por e-mail o comprovante do pagamento.
3. Ao término do fluxo de pagamento, o aplicativo do parceiro recebe as informações sobre o pagamento realizado e volta a ser executado na tela da Cielo LIO.

Para mais informações e detalhes sobre o desenvolvimento de um aplicativo para a Cielo LIO, acesse a documentação Desenvolva um App para LIO.

### Ambiente Sandbox - Emulador

Este aplicativo funciona como um proxy de todas as chamadas que o SDK enviaria para a LIO. Ele simplesmente trata essas chamadas e simula os tipos possíveis de retorno para a aplicação cliente, permitindo que o desenvolvedor realize os testes nos métodos do SDK e faça o debug da sua aplicação durante o desenvolvimento e a integração com o Cielo LIO Order Manager SDK, sem a necessidade de possuir um hardware da Cielo LIO.

> Não é nem mesmo necessário deixar esse app aberto. Assim que a aplicação cliente utilizar uma função do OrderManager que chamaria a LIO, essa aplicação entra em primeiro plano para seleção do que deve ser retornado (Sucesso, Erro ou Cancelamento)

Faça o download do .apk do emulador no link abaixo:

[Download do Emulador](https://s3-sa-east-1.amazonaws.com/cielo-lio-store/apps/lio-emulator/1.1.0/lio-emulator.apk)

Para instalar o aplicativo, basta seguir os seguintes passos:

- Acesse as configurações do seu aparelho e vá até o menu “Segurança”;
- Localize o item “Fontes desconhecidas”, na seção “Administração do dispositivo”
- Toque na chave ao lado e confirme sua escolha para passar a permitir a instalação de arquivos APK baixados por fontes alternativas.

### Configurar módulos e dependências

Efetue o download da nossa SDK [CLIQUE AQUI](https://desenvolvedores.cielo.com.br/api-portal/pt-br/content/sdk-cielo-lio)

Disponilizamos dois diretorios `com` e `cielo` basta copiá-los para o seu repositório `.m2/repository`

Exemplo:

No Windows: `C:\Users\<usuario>\.m2`

No Linux: `/home/<usuario>/.m2`

No Mac: `/Users/<usuario>/.m2`

No **build.gradle** na raiz do seu projeto, adicionar o `mavenLocal()` dentro da closure **allprojects** conforme abaixo:

```
allprojects {  
  repositories {  
    mavenLocal()  
	... 
  }  
}
```

No **build.gradle** do app, adicionar a dependência dentro da closure **dependencies** conforme abaixo:

```
dependencies {    
  implementation 'com.cielo.lio:order-manager:1.7.1'  
}
```

Após essas configurações realize o build do projeto.

última versão 1.7.1

> A partir da versão 1.17.7-beta do SDK se tornou necessária a permissão de INTERNET na aplicação cliente.
> Adicione essa permissão ao `AndroidManifest.xml` da sua aplicação.

### Credenciais

Para utilizar o Cielo LIO Order Manager SDK, é necessário inserir as seguintes credenciais na inicialização do OrderManager:

```java
Credentials credentials = new Credentials("Seu client id aqui", "Seu accessToken aqui");
```

- Client-Id
  Identificação de acesso. Sua geração ocorre no momento da criação pelo painel do desenvolvedor. Seu valor pode ser visualizado na coluna Client ID, dentro do menu ['Client ID Cadastrados'](https://desenvolvedores.cielo.com.br/api-portal/myapps)

- Access-Token
  Identificação do token de acesso, que armazena as regras de acesso permitidas ao Client ID. Sua geração ocorre no momento da criação do Client ID pelo painel do desenvolvedor. Seu valor pode ser visualizado clicando em 'detalhes' na coluna 'Access Tokens', dentro do menu ['Client ID Cadastrados'](https://desenvolvedores.cielo.com.br/api-portal/myapps)

[Crie sua Credencial para a API Local](https://desenvolvedores.cielo.com.br/api-portal/myapps/new)

## Criação e Gerenciamento de Ordens

O fluxo básico para utilização do SDK pode ser dividido em 7 etapas, conforme o diagrama abaixo:

![order manager sdk fluxo](https://desenvolvedores.cielo.com.br/api-portal/sites/default/files/fluxogram-lio-sdk.png)

Abaixo, iremos mostrar como realizar cada uma dessas etapas.

### Criar OrderManager

Este método permite iniciar o OrderManager, que é responsável pelas principais operações do Cielo LIO Order Manager SDK. O OrderManager representa a interface com a API REST do Order Manager.

Tudo começa com a criação do OrderManager!

```java
OrderManager orderManager = new OrderManager(credentials, context);
```

O construtor do OrderManager recebe 2 parâmetros:

| Atributo    | Descrição                                                                                                                                                                                                                                                                                              | Domínio                       |
| ----------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ----------------------------- |
| credentials | Credenciais de acesso do Parceiro/Desenvolvedor. Depois de se registrar no Portal de Desenvolvedores e criar seu app, o Parceiro/Desenvolvedor recebe as credenciais (Client-Id e Access-Token). Elas servem para identificá-lo unicamente na plataforma LIO e diferenciar as ordens da sua aplicação. | `cielo.sdk.order.Credentials` |
| context     | O contexto da aplicação.                                                                                                                                                                                                                                                                               | `android.content.Context`     |

> **Atenção:** Recomendamos criar/inicializar o OrderManager no aplicativo do parceiro no momento em que for utilizar as funcionalidades, visando otimizar o desempenho do aplicativo

### Vincular o contexto da aplicação ao SDK

Com o método `bind()`, é possível vincular o contexto da aplicação ao SDK. Este serviço é responsável por gerenciar as funções relacionadas com as ordens da LIO

```java
ServiceBindListener serviceBindListener = new ServiceBindListener() {

    @Override public void onServiceBoundError(Throwable throwable) {
        //Ocorreu um erro ao tentar se conectar com o serviço OrderManager
    }

    @Override
    public void onServiceBound() {
        //Você deve garantir que sua aplicação se conectou com a LIO a partir desse listener
        //A partir desse momento você pode utilizar as funções do OrderManager, caso contrário uma exceção será lançada.
    }

    @Override
    public void onServiceUnbound() {
        // O serviço foi desvinculado
    }
};

orderManager.bind(context, serviceBindListener);
```

> **Atenção:** Você deve garantir que o listener `onServiceBound()` foi chamado antes de utilizar as funções do OrderManager, caso contrário uma exceção será lançada e causará um crash na sua aplicação.

O método Bind recebe dois parâmetros:

- **context:** objeto que proverá o contexto em que o serviço será vinculado.

- **serviceBindListener:** listener que notifica o estado da conexão com o serviço OrderManager.

Com o OrderManager inicializado e após a execução do método onServiceBound, se torna possível ir para a próxima etapa e criar um pedido (classe Order).

### Criar um pedido

A Cielo LIO trabalha com o conceito de Order (pedido). É necessário possuir um pedido para então realizar o(s) pagamento(s).

Este método permite criar uma Order (classe Order). Para realizar essa criação, utilize o método createDraftOrder, que disponibilizará uma Order com status DRAFT:

```java
Order order = orderManager.createDraftOrder("REFERÊNCIA_DO_PEDIDO");
```

### Adicionar itens em um pedido

Este método permite que sejam adicionados itens em um pedido.

> **Atenção:** É necessário adicionar no mínimo um item a um pedido para que seja possível dar continuidade ao pagamento.

```java
// Identificação do produto (Stock Keeping Unit)
String sku = "2891820317391823";
String name = "Coca-cola lata";

// Preço unitário em centavos
int unitPrice = 550;
int quantity = 3;

// Unidade de medida do produto String
unityOfMeasure = "UNIDADE";

order.addItem(sku, name, unitPrice, quantity, unityOfMeasure);
```

### Liberar pedido para pagamento

Este método permite atualizar o status de um pedido e liberá-lo para pagamento. O objetivo é utilizar este método depois de adicionar todos os itens no pedido.

```java
orderManager.placeOrder(order);
```

Após a execução desse método, o status da Order irá trocar de DRAFT para ENTERED, permitindo que a mesma seja paga.

> **Atenção:** É importante observar que, uma vez que uma Order tenha mudado de status para ENTERED, não será mais possível incluir itens na mesma.
> Iniciar processo de pagamento

Este método permite iniciar o processo de pagamento na Cielo LIO.

## Processo de Pagamento

Estão disponíveis algumas formas de chamar o método checkoutOrder:

![checkoutorder formas](https://desenvolvedores.cielo.com.br/api-portal/sites/default/files/checkoutOrder.jpg)

Independende da forma escolhida, você deverá utilizar o seguinte callback como parâmetro do método de `checkoutOrder()` para receber os estados relacionados ao pagamento.:

```java
PaymentListener paymentListener = new PaymentListener() {
    @Override
    public void onStart() {
        Log.d("SDKClient", "O pagamento começou.");
    }

    @Override
    public void onPayment(@NotNull Order order) {
        Log.d("SDKClient", "Um pagamento foi realizado.");
    }

    @Override public void onCancel() {
        Log.d("SDKClient", "A operação foi cancelada.");
    }

    @Override public void onError(@NotNull PaymentError paymentError) {
        Log.d("SDKClient", "Houve um erro no pagamento.");
    }
};
```

> PaymentListener: Um callback que informa sobre todas as ações tomadas durante o processo de pagamento.
> As seguintes ações podem ser notificadas:  
> • onStart - Quando se dá o início do pagamento.
> • onPayment - Quando um pagamento é realizado. Notem que um pedido pode ser pago por mais de um pagamento.
> • onCancel - Quando o pagamento é cancelado.
> • onError - Quando acontece um erro no pagamento do pedido.

O método `onPayment()` retorna um objeto `Order` com uma lista de `Payment` que possui um HashMap `PaymentFields` com a maioria dos dados da transação.
Segue abaixo a tabela com os dados mais relevantes existentes nesse mapa:

| Payment Fields - (**atributo do objeto Payment**) |
| Nome do Campo | Descrição do Campo | Valor Exemplo |
|--------------------------|--------------------------------------------------------------------|------------------------------------------|
| clientName | Nome do Portador | VISA ACQUIRER TEST CARD 03 |
| hasPassword | Validar se a operação pediu senha | true |
| primaryProductCode | Código do produto primário | 4 |
| primaryProductName | Nome do produto primário | CREDITO |
| upFrontAmount | Valor da entrada da transação | 2500 |
| creditAdminTax | Valor da taxa de administração de crédito | 0 |
| firstQuotaDate | Data de débito da primeira parcela | 25/12/2018 (dd/MM/yyy) |
| externalCallMerchantCode | Número do Estabelecimento Comercial | 0010000244470001 |
| hasSignature | Validar se a operação pediu assinatura | false |
| hasPrintedClientReceipt | Validar se imprimiu a via do cliente | false |
| applicationName | Pacote da aplicação | cielo.launcher |
| interestAmount | Valor de juros | 5000 |
| changeAmount | Valor de troco | 4500 |
| serviceTax | Taxa de serviço | 2000 |
| cityState | Cidade - Estado | Barueri - SP |
| v40Code | Tipo da transação | 5 (Lista de Tipos de transação abaixo) |
| secondaryProductName | Nome do produto secundario | PARC. ADM |
| paymentTransactionId | ID da transação de pagamento | 4c603b44-19b8-497c-b072-60d5dd6807e7 |
| bin | Número cartão tokenizado (6 primeiros dígitos – 4 últimos dígitos ou 4 últimos) | 476173-0036 ou \*\*\*\*\*\*\*\*\*\*4242 |
| originalTransactionId | ID da transação original, nos casos de cancelamento | 729d32ac-6c8d-4b0c-b670-263552f07000 |
| cardLabelApplication | Tipo de aplicação utilizada pelo cartão na transação | CREDITO DE VISA |
| secondaryProductCode | Código do produto secundário | 205 |
| documentType | (J) = Pessoa Jurídica (F) = Pessoa Física | J |
| statusCode | Status da transação1 - Autorizada 2 - Cancelada | 1 |
| merchantAddress | Endereço do estabelecimento comercial (lojista) | Alameda Grajau, 219 |
| merchantCode | Número do Estabelecimento Comercial | 0010000244470001 |
| hasConnectivity | Valida se a transação foi online | true |
| productName | forma de pagamento compilada | CREDITO PARCELADO ADM - I |
| merchantName | Nome Fantasia do Estabelecimento Comercial | LOJA ON |
| firstQuotaAmout | Valor da primeira parcela | 0 |
| cardCaptureType | Código do tipo de captura do cartão | 0 (Lista de Tipos de Captura abaixo) |
| requestDate | Data da requisição em milisegundos | 1293857600000 |
| boardingTax | Taxa de embarque | 1200 |
| applicationId | Pacote de aplicação | cielo.launcher |
| numberOfQuotas | Número de parcelas | 2 |

| Lista de Tipos de Captura |
| Código | Valor |
|---------------------------|----------------------------------|
| 0 | Transação com cartão de chip |
| 1 | Transação digitada |
| 2 | Transação com cartão de tarja |
| 3 | Transação com cartão contactless |
| 6 | Transação com QRCode |

| Lista de Tipos de Transação - (**Isto não é válido como código primário**)|
| Código | Valor |
|-----------------------------|----------------------------------|
| 4 | Crédito à vista |
| 5 | Crédito parcelado administrativo |
| 6 | Crédito parcelado loja |
| 7 | Pré autorização |
| 8 | Débito à vista |
| 9 | Débito pré datado |
| 10 | Crediário venda |
| 11 | Crediário simulação |
| 13 | Voucher alimentação/refeição |
| 28 | Cancelamento de venda |

| Você pode pegar outros dados diretamente do (**objeto Payment**)|
| Campo | Descrição do campo |
|-----------------------------|----------------------------------|
| authCode | Cód de Autorização(AUT do recibo)|
| cieloCode | Campo DOC do recibo (NSU) |
| brand | Bandeira do Cartão |

Exemplo de código para trazer a bandeira e transação:

```java
@Override

public void onPayment(Order paidOrder) {

    Log.d(TAG, "ON PAYMENT");
    order = paidOrder;

    order.markAsPaid();

    orderManager.updateOrder(order);

    Payment pgTO = order.getPayments().get(0);
    bandeira = getBandeira(pgTO.getPaymentFields().get("brand"));
    transacao = pgTO.getPaymentFields().get("authCode");

    pagamento.setBandeira(bandeira);
    pagamento.setAutorizacao(transacao);
```

> Todos os valores financeiros são informados sem vírgula, ou seja 2500 são equivalentes a R$ 25,00.

### 1. Requisição de pagamento

Para realizar um request de pagamento é preciso informar, pelo menos, o OrderId criado anteriormamente. Dependendo dos parametros informados na hora de montar o request de pagamento, um dos processos citados acima será iniciado. No Pagamento parcial, o valor do pagamento é informado dentro do fluxo de telas da Cielo LIO. Na sequência, o fluxo de pagamento da Cielo LIO é iniciado (definir o valor a ser pago, escolher a forma de pagamento, inserir o cartão, digitar a senha e visualizar e/ou enviar por e-mail o comprovante).

```java
CheckoutRequest request = new CheckoutRequest.Builder()
                    .orderId(order.getId()) /* Obrigatório */
                    .amount(123456789) /* Opcional */
                    .ec("999999999999999") /* Opcional (precisa estar habilitado na LIO) */
                    .installments(3) /* Opcional */
                    .email("teste@email.com") /* Opcional */
                    .paymentCode(PaymentCode.CREDITO_PARCELADO_LOJA) /* Opcional */
                    .build();
```

> **Atenção:** Os dados dos parâmetros EC e EMAIL são apenas exemplos e não deve ser utilizado em sua chamada. Para a utilização do EC na chamada, é necessário uma pré habilitação junto a Cielo, para o recebimento com MULTI-EC.

| Atributo     | Descrição                                                        | Domínio                               |
| ------------ | ---------------------------------------------------------------- | ------------------------------------- |
| orderId      | O identificador do pedido a ser pago.                            | `String`                              |
| amount       | Valor a ser pago. R$ 10,00 deve ser enviado como 1000.           | `Long`                                |
| ec           | Em casos de multi-ec, informar aqui o número correspondente      | `String`                              |
| installments | número de parcelas. para pagamento a vista, não precisa informar | `Int`                                 |
| email        | email pra onde será enviado o comprovante                        | `String`                              |
| paymentCode  | Código da operação de pagamento                                  | `cielo.sdk.order.payment.PaymentCode` |

```java
orderManager.checkoutOrder(request, paymentListener);
```

Nessa forma de pagamento, é necessário apenas fazer a chamada do método enviando os seguintes parâmetros:

| Atributo        | Descrição                                             | Domínio                                   |
| --------------- | ----------------------------------------------------- | ----------------------------------------- |
| request         | O objeto contendo as informações da ordem a ser paga. | `cielo.orders.domain.CheckoutRequest`     |
| paymentListener | Callback de pagamento.                                | `cielo.sdk.order.payment.PaymentListener` |

> **Atenção:** Na versão 0.19.0 do SDK foram introduzidos códigos de pagamento para as principais operações disponibilizadas na LIO, facilitando assim as chamadas de pagamento. É importante ressaltar que as formas de pagamento são configuradas de acordo com o estabelecimento comercial, e nem todas estarão disponíveis em todas os terminais. Caso o método de pagamento selecionado não esteja disponível na LIO em questão, ocorrerá uma exceção do tipo `NoSuchElementException`.

| PaymentCode                |
| -------------------------- |
| DEBITO_AVISTA              |
| DEBITO_PAGTO_FATURA_DEBITO |
| CREDITO_AVISTA             |
| CREDITO_PARCELADO_LOJA     |
| CREDITO_PARCELADO_ADM      |
| CREDITO_PARCELADO_BNCO     |
| PRE_AUTORIZACAO            |
| CREDITO_PARCELADO_CLIENTE  |
| CREDITO_CREDIARIO_CREDITO  |
| VOUCHER_ALIMENTACAO        |
| VOUCHER_REFEICAO           |
| VOUCHER_AUTOMOTIVO         |
| VOUCHER_CULTURA            |
| VOUCHER_PEDAGIO            |
| VOUCHER_BENEFICIOS         |
| VOUCHER_AUTO               |
| VOUCHER_CONSULTA_SALDO     |
| VOUCHER_VALE_PEDAGIO       |
| CREDIARIO_VENDA            |
| CREDIARIO_SIMULACAO        |
| CARTAO_LOJA_AVISTA         |
| CARTAO_LOJA_PARCELADO_LOJA |
| CARTAO_LOJA_PARCELADO      |
| CARTAO_LOJA_PARCELADO_BANCO|
| CARTAO_LOJA_PAGTO_FATURA_CHEQUE|
| CARTAO_LOJA_PAGTO_FATURA_DINHEIRO|
| FROTAS                     |
| PIX (EM PILOTO)            |

> **Atenção:** A utilização do paymentCode referente ao PIX estará disponivel para uso a partir da versão 1.7.0 da SDK e será liberado para utilização no inicio de Dezembro/2023. É necessário que esse meio de pagamento esteja habilitado junto a Cielo.

Abaixo, segue um exemplo do fluxo com as telas exibidas informando apenas o OrderId:
![fluxo parcial](https://desenvolvedores.cielo.com.br/api-portal/sites/default/files/pagamento-parcial.jpg)

Abaixo, segue um exemplo do fluxo com as telas exibidas informando o OrderId e o Valor:
![fluxo parcial](https://desenvolvedores.cielo.com.br/api-portal/sites/default/files/fluxo-pagamento-valores.jpg)

Abaixo, segue um exemplo do fluxo com as telas exibidas informando o OrderId, o Valor e o código de pagamento:
![fluxo parcial](https://desenvolvedores.cielo.com.br/api-portal/sites/default/files/pagamento-direto.jpg)

Abaixo, segue um exemplo do fluxo com as telas exibidas informando o OrderId, o Valor, o código de pagamento e o número de parcelas:
![fluxo parcial](https://desenvolvedores.cielo.com.br/api-portal/sites/default/files/pagamento-parcelado.jpg)

Abaixo, segue um exemplo do fluxo com as telas exibidas informando o OrderId, o Valor, o código de pagamento, o número de parcelas e o email:
![fluxo parcial](https://desenvolvedores.cielo.com.br/api-portal/sites/default/files/pagamento-parcelado.jpg)

Abaixo, segue um exemplo do fluxo com as telas exibidas informando o OrderId, o Valor, o código de pagamento, o número de parcelas, o EC e o email
![fluxo parcial](https://desenvolvedores.cielo.com.br/api-portal/sites/default/files/pagamento-parcelado.jpg)

## Pagamento Parcial

Existe um produto Cielo habilitado em estabelecimentos que permite o pagamento apenas com o saldo que o cliente possui no cartão

Exemplo: Se um pedido tiver o valor de R$ 100,00, mas o cliente possui um limite em seu cartão de apenas R$50,00 a maquina receberá esse valor, sendo de suma importância a aplicação validar o acontecimento desse cenário para tratativas de cancelamento do pagamento ou de receber o restante de outra forma.

### Tratando Cenário

Através do listener de retorno do pagamento `OnPayment` é possível acessar informações do objeto Order para confirmar se existe algum valor pendente de pagamento. Abaixo segue os campos que utilizaremos para identificar o cenário de pagamento Parcial:

| Atributo        | Descrição                                                                     |
| --------------- | ----------------------------------------------------------------------------- |
| `pendingAmount` | Esse campo retorna o valor que ainda está pendente de pagamento para o pedido |
| `paidAmount`    | Esse é o valor pago no pedido                                                 |
| `price`         | Esse é o valor pago no pedido                                                 |

Será necessário recuperar e validar o campo `pendingAmount` esse campo é responsável por indicar se existe algum valor pendente para o pedido. Caso o valor desse campo seja igual a 0 a aplicação poderá seguir o seu fluxo

Ainda é possível efetuar a lógica sobre o campo `paidAmount` , caso o valor desse campo seja o esperado para o pedido/pagamento poderá seguir normalmente o fluxo da aplicação.

Exemplo de lógica:

```java
PaymentListener paymentListener = new PaymentListener() {
      @Override
      public void onStart() {
          Log.d("SDKClient", "O pagamento começou.");
      }

      @Override
      public void onPayment(@NotNull Order order) {
          Log.d("SDKClient", "Um pagamento foi realizado.");
          if (order.pendingAmout() == 0) {
            order = paidOrder;
            order.markAsPaid();
            orderManager.updateOrder(order);
        } else {
            // PEDIDO AINDA TEM VALOR A SER RECEBIDO DEVERÁ SER TRATADO PELA APLICAÇÃO DO PARCEIRO
        }
      @Override public void onCancel() {
          Log.d("SDKClient", "A operação foi cancelada.");
      }

      @Override public void onError(@NotNull PaymentError paymentError) {
          Log.d("SDKClient", "Houve um erro no pagamento.");
      }
  };
```

Exemplo dos campos retornados:

```json
{
  "createdAt": "Nov 29, 2022:21:32 PM",
  "id": "508da29c-9447-4aaf-ab5f-78169986d1eb",
  "items": [
    {
      "description": "",
      "details": "",
      "id": "67343d1a-055c-4fda-8432-ef32fed4ddb9",
      "name": "Coca-cola lata",
      "quantity": 1,
      "reference": "",
      "sku": "2891820317391823",
      "unitOfMeasure": "litro",
      "unitPrice": 550
    }
  ],
  "notes": "",
  "number": "",
  "paidAmount": 100,
  "payments": [
    {
      "accessKey": "izEd7gEBaWjNmzTr0ccE6PZnbWCOHrJahp6oZ2f1QYTWaF9ivI/ wPEY0z5pbk4L8jWlcixeDbd5tHb28quVpTdX2e8pebwOO84DbT",
      "amount": 1000,
      "applicationName": "CieloLIO",
      "authCode": "ab14d87f-c4e7-49f4-ac1f-90ae97f80d3f",
      "brand": "mock_brand",
      "cieloCode": "1a561528-7345-4ab8-a3e2-c8e8c87787ae",
      "description": "mock_description",
      "discountedAmount": 0,
      "externalId": "mock_externalId",
      "id": "eb785f2c-8286-46cc-8829-b10a7280dcf7",
      "installments": 1,
      "mask": "mock_mask",
      "merchantCode": "1234",
      "paymentFields": {
        "clientName": "CieloLio",
        "betterDate": "121212121",
        "upFrontAmount": "1000",
        "creditAdminTax": "500",
        "firstQuotaDate": "12121212121",
        "hasSignature": "true",
        "hasPrintedClientReceipt": "true",
        "hasWarranty": "false",
        "interestAmount": "0",
        "serviceTax": "0",
        "cityState": "Rio de Janeiro - RJ",
        "hasSentReference": "true",
        "cardLabelApplication": "llalal",
        "signatureMd5": "wedsaee12jnlk3n12lk3n2kl13n1lk23nk12ln3l",
        "hasConnectivity": "true",
        "productName": "lalala",
        "finalCryptogram": "123123213",
        "entranceMode": "????",
        "firstQuotaAmount": "10000",
        "cardCaptureType": "2",
        "requestDate": "121212121",
        "boardingTax": "0",
        "numberOfQuotas": "1",
        "isDoubleFontPrintAllowed": "true",
        "token": "1212121",
        "hasPassword": "true",
        "primaryProductCode": "8",
        "isExternalCall": "true",
        "primaryProductName": "DEBITO",
        "receiptPrintPermission": "3",
        "isOnlyIntegrationCancelable": "false",
        "externalCallMerchantCode": "123",
        "isFinancialProduct": "false",
        "applicationName": "CieloLIO",
        "changeAmount": "0",
        "v40Code": "2",
        "secondaryProductName": "DEBITO A VISTA - I",
        "paymentTransactionId": "cod0001",
        "typeName": "VENDA",
        "avaiableBalance": "2000",
        "pan": "4242424242424242",
        "secondaryProductCode": "208",
        "hasSentMerchantCode": "true",
        "documentType": "2144",
        "statusCode": "2",
        "merchantAddress": "dsdfdsf",
        "merchantCode": "1234",
        "paymentTypeCode": "1",
        "merchantName": "12345",
        "totalizerCode": "3",
        "applicationId": "Cielo LIO",
        "signatureBytes": "ijodiqjiojio2j3io1j3io21j3io1j3oi21j312oij312312",
        "reference": "112121"
      },
      "primaryCode": "8",
      "requestDate": "121212121",
      "secondaryCode": "208",
      "terminal": "mock_terminal"
    }
  ],
  "pendingAmount": 450,
  "price": 550,
  "reference": "teste1",
  "status": "ENTERED",
  "type": "PAYMENT",
  "updatedAt": "Mar 6, 2018 2:21:32 PM"
}
```

## Cancelamento

Para tratar a resposta do cancelamento, você deverá utilizar o seguinte callback como parâmetro do método de `cancelOrder()` para receber os estados relacionados ao cancelamento.

> CancellationListener: Um callback que informa sobre todas as ações tomadas durante o processo de cancelamento.
> As seguintes ações pode ser notificadas:
> • onSuccess - Quando um cancelamento é realizado com sucesso.
> • onCancel - Quando o usuário cancela a operação.
> • onError - Quando acontece um erro no cancelamento do pedido.

```java
CancellationListener cancellationListener = new CancellationListener() {
    @Override
    public void onSuccess(Order cancelledOrder) {
        Log.d("SDKClient", "O pagamento foi cancelado.");
    }

    @Override
    public void onCancel() {
        Log.d("SDKClient", "A operação foi cancelada.");
    }

    @Override
    public void onError(PaymentError paymentError) {
        Log.d("SDKClient", "Houve um erro no cancelamento");
    }
});
```

### Cancelar Pagamento

> **ATENÇÃO:** Hoje só é possivel realizar o cancelamento total do pagamento, para realizar o cancelamento parcial é necessário utilizar a interface da LIO.

No método Cancelar um Pagamento, é necessário ter salvo uma instância da Order que contém as informações da Order. Essa Order pode ser recuperada no sucesso do callback do pagamento ou usando o método de Listagem de Pedidos (Orders) (link para método).
Assim que possuir a instância da Order, utilize o método abaixo passando os parâmetros conforme o exemplo abaixo:

```java
CancellationRequest request = new CancellationRequest.Builder()
                .orderId(order.getId()) /* Obrigatório */
                .authCode(order.getPayments().get(0).getAuthCode()) /* Obrigatório */
                .cieloCode(order.getPayments().get(0).getCieloCode()) /* Obrigatório */
                .value(order.getPayments().get(0).getAmount()) /* Obrigatório */
                .ec("0000000000000003") /* Opcional */
                .build();
```

> **Atenção:** O dado do parâmetro EC é apenas um exemplo e não deve ser utilizado em sua chamada. Para a utilização do EC na chamada de cancelamento, é necessário uma pré habilitação junto a Cielo.

Abaixo é detalhado cada um dos parâmetros enviados no método:

| Atributo             | Descrição                                                                             | Domínio                                        |
| -------------------- | ------------------------------------------------------------------------------------- | ---------------------------------------------- |
| orderID              | O identificador do pedido a ser pago.                                                 | `String`                                       |
| authCode             | Código de autorização do pagamento.                                                   | `String`                                       |
| cieloCode            | NSU do pagamento                                                                      | `String`                                       |
| value                | Valor do pagamento a ser cancelado                                                    | `Long`                                         |
| ec                   | Em casos de multi-ec, informar aqui o número correspondente                           | `String`                                       |
| cancellationListener | Callback que informa sobre todas as ações tomadas durante o processo de cancelamento. | `cielo.sdk.order.payment.CancellationListener` |

```java
orderManager.cancelOrder(request, cancellationListener);
```

Abaixo é detalhado cada um dos parâmetros enviados no método:

| Atributo             | Descrição                                                                             | Domínio                                        |
| -------------------- | ------------------------------------------------------------------------------------- | ---------------------------------------------- |
| request              | O objeto contendo as informações da ordem a ser cancelada.                            | `cielo.orders.domain.CancellationRequest`      |
| cancellationListener | Callback que informa sobre todas as ações tomadas durante o processo de cancelamento. | `cielo.sdk.order.payment.CancellationListener` |

Exemplo de JSON retornado no listener `OnSucess()` do CancellationListener.

```json
{
	"createdAt":"2023-11-03T10:28:52.164Z",
	"id":"0f156449-6c77-47aa-88b7-da98edaefc16",
	"items":[
		{
			"description":"",
			"details":"",
			"id":"7243e387-d73a-4f70-bc9b-8b39e1e1f90a",
			"name":"TESTE",
			"quantity":1,
			"reference":"",
			"sku":"0000000000",
			"unitOfMeasure":"UN",
			"unitPrice":"10"
		}
	],
	"notes":"",
	"number":"",
	"paidAmount":"0",
	"payments":[
		{
			"accessKey":"BoV29dYpLriYeTdpSOOHXJOWSvQpU8Srt97xlGzwj8bkPChegC",
			"amount":"10",
			"applicationName":"pdv.teste",
			"authCode":"285565",
			"brand":"VISA",
			"cieloCode":"74356",
			"description":"",
			"discountedAmount":"0",
			"externalId":"e9c93039-3212-4f25-b571-2c57c0259e5b",
			"id":"8083f635-13f4-48e4-9565-3876990d5f93",
			"installments":"0",
			"mask":"******0759",
			"merchantCode":"0000701260460001",
			"paymentFields":{
				"upFrontAmount":"0",
				"creditAdminTax":"0",
				"firstQuotaDate":"0",
				"hasSignature":"false",
				"hasPrintedClientReceipt":"false",
				"hasWarranty":"false",
				"interestAmount":"0",
				"serviceTax":"0",
				"cityState":"SAO PAULO SP",
				"hasSentReference":"false",
				"cardLabelApplication":"CREDITO         ",
				"originalTransactionId":"0",
				"originalTransactionDate":"03/11/23",
				"hasConnectivity":"true",
				"productName":"CREDITO A VISTA",
				"finalCryptogram":"74507261AE81C147",
				"entranceMode":"691017207380",
				"firstQuotaAmount":"0",
				"cardCaptureType":"3",
				"requestDate":"1699018133112",
				"boardingTax":"0",
				"numberOfQuotas":"0",
				"isDoubleFontPrintAllowed":"false",
				"bin":"481135",
				"hasPassword":"false",
				"isExternalCall":"true",
				"primaryProductCode":"1000",
				"primaryProductName":"CREDITO",
				"isOnlyIntegrationCancelable":"false",
				"receiptPrintPermission":"1",
				"isFinancialProduct":"true",
				"applicationName":"teste.pdv",
				"changeAmount":"0",
				"v40Code":"4",
				"paymentTransactionId":"e9c93039-3212-4f25-b571-2c57c0259e5b",
				"secondaryProductName":"A VISTA",
				"avaiableBalance":"0",
				"typeName":"VENDA A CREDITO",
				"pan":"******0759",
				"secondaryProductCode":"1",
				"hasSentMerchantCode":"false",
				"documentType":"J",
				"merchantAddress":"AV TESTE 200",
				"statusCode":"1",
				"merchantCode":"0000000000",
				"paymentTypeCode":"1",
				"merchantName":"TESTE",
				"totalizerCode":"10",
				"applicationId":"cielo.launcher",
				"document":"58731662000111"
			},
			"primaryCode":"1000",
			"requestDate":"1699018133112",
			"secondaryCode":"1",
			"terminal":"00876192"
		},
		{
			"accessKey":"",
			"amount":"10",
			"applicationName":"pdv.teste",
			"authCode":"285565",
			"brand":"VISA",
			"cieloCode":"74362",
			"description":"",
			"discountedAmount":"0",
			"externalId":"e5cb2957-3a4d-49af-be33-e99d6ae5c806",
			"id":"7ff66130-4836-4d58-9d9f-12a57a2b630d",
			"installments":"0",
			"mask":"******0759",
			"merchantCode":"00000000000000",
			"paymentFields":{
				"upFrontAmount":"0",
				"creditAdminTax":"0",
				"firstQuotaDate":"0",
				"hasSignature":"false",
				"hasPrintedClientReceipt":"false",
				"hasWarranty":"false",
				"interestAmount":"0",
				"serviceTax":"0",
				"cityState":"SAO PAULO SP",
				"hasSentReference":"false",
				"cardLabelApplication":"CREDITO         ",
				"originalTransactionId":"74356",
				"originalTransactionDate":"03/11/23",
				"hasConnectivity":"true",
				"productName":"CREDITO A VISTA",
				"entranceMode":"691017207380",
				"firstQuotaAmount":"0",
				"cardCaptureType":"3",
				"requestDate":"1699018209429",
				"boardingTax":"0",
				"numberOfQuotas":"0",
				"isDoubleFontPrintAllowed":"false",
				"bin":"481135",
				"hasPassword":"false",
				"isExternalCall":"true",
				"primaryProductCode":"1000",
				"primaryProductName":"CREDITO",
				"isOnlyIntegrationCancelable":"false",
				"receiptPrintPermission":"1",
				"externalCallMerchantCode":"0000000000",
				"isFinancialProduct":"true",
				"applicationName":"teste.pdv",
				"changeAmount":"0",
				"v40Code":"28",
				"paymentTransactionId":"e9c93039-3212-4f25-b571-2c57c0259e5b",
				"secondaryProductName":"A VISTA",
				"avaiableBalance":"0",
				"typeName":"VENDA A CREDITO",
				"pan":"******0759",
				"secondaryProductCode":"1",
				"hasSentMerchantCode":"false",
				"documentType":"J",
				"merchantAddress":"AV TESTE 200",
				"statusCode":"2",
				"merchantCode":"000000000",
				"paymentTypeCode":"1",
				"merchantName":"TEST MERCHANT",
				"totalizerCode":"10",
				"applicationId":"pdv.teste",
				"document":"58731662000111"
			},
			"primaryCode":"1000",
			"requestDate":"1699018209429",
			"secondaryCode":"1",
			"terminal":"00876192"
		}
	],
	"pendingAmount":"10",
	"price":"10",
	"reference":"2052023110310274434",
	"releaseDate":null,
	"status":"ENTERED",
	"type":"PAYMENT",
	"updatedAt":"2023-11-03T10:30:17.518Z"
}
```

### Receber aviso de cancelamento feito na LIO

Toda vez que é feito um cancelamento na LIO utilizando o launcher, o sistema envia um broadcast notificando quaisquer aplicações que estejam registradas sobre o mesmo. Para que sua aplicação seja notificada de um cancelamento, atualize o arquivo `AndroidManifest.xml` conforme o exemplo abaixo:

![Manifest]({{ site.baseurl_root }}/images/cielo-lio/manifest.jpg)

em seguida, é necessário implementar um `BroadcastReceiver` para tratar o evento da maneira que for pertinente a sua aplicação:

```java

public class LIOCancelationBroadcastReceiver extends BroadcastReceiver {

    String MY_CLIENT_ID = "Seu client id aqui";
    String MY_ACCESS_KEY = "Seu access key aqui";

    String INTENT_ORDER_KEY = "ORDER";
    String INTENT_TRANSACTION_KEY = "TRANSACTION";

    @Override
    public void onReceive(Context context, Intent intent) {
        ParcelableOrder order = intent.getExtras().getParcelable(INTENT_ORDER_KEY);

        if(MY_ACCESS_KEY.equalsIgnoreCase(order.getAccessKey()) && MY_CLIENT_ID.equalsIgnoreCase(order.getSecretAccessKey())) {
            ParcelableTransaction transaction  = intent.getExtras().getParcelable(INTENT_TRANSACTION_KEY);
        }
    }
}

```

## Listagem de Pedidos

Na listagem de pedidos, é possível obter todas os pedidos (Orders) abertas na Cielo LIO pelo aplicativo do parceiro.
Para isso, basta utilizar a função abaixo, que retorna os pedidos de forma paginada:

```java
//Busca 10 items da primeira página
ResultOrders resultOrders = orderManager.retrieveOrders(10, 0);
```

| ATRIBUTO | DESCRIÇÃO                                    | DOMÍNIO   |
| -------- | -------------------------------------------- | --------- |
| pageSize | quantidade de items por página               | `Integer` |
| page     | número da página requistada (iniciando em 0) | `Integer` |

> O objeto ResultOrders contém uma lista com todas as ordens abertas assinadas com as credenciais da aplicação.

## Finalizar uso do OrderManager

Após executar todas as operações de pagamento e caso não seja necessário utilizar o objeto orderManager, utilize método unbind para desvincular o contexto e evitar problemas de integridade.

Fique atento ao local onde o `unbind()` será executado para evitar problemas com ciclo de vida da Activity ou Fragment que foi vinculado ao serviço.
É importante lembrar que se o contexto for alterado, é preciso desvincular o serviço (ex.: troca de Activity)
Utilize o método abaixo para finalizar o uso do OrderManager:

```java
orderManager.unbind();
```

## Informações do terminal

Todas as informações referentes ao terminal, que foram expostas, estão disponíveis no InfoManager

```java
InfoManager infoManager = new InfoManager();
```

### Nível de Bateria

Para consultar o nível de carga da LIO, basta utilizar o métdo abaixo:

```java
infoManager.getBatteryLevel(context);
```

> O valor da bateria será retornado em uma `Float` contendo um valor de 0 a 1 em caso de sucesso e -1 em caso de erro.

### Verificar Modelo da LIO

O SDK disponibiliza um método para verificar qual modelo da LIO o app está instalado. Basta acessar da seguinte forma:

```java
infoManager.getDeviceModel();
```

O mesmo irá retornar um enum do tipo `DeviceModel` com o modelo correspondente.

### Obtendo informações do usuário (EC e Número Lógico)

Através do SDK, é possível recuperar os dados do cliente e número lógico de maneira simples utilizando o método abaixo:

```java
infoManager.getSettings(context);
```

> Esta função, retornará um objeto do tipo Settings onde é possível recuperar as informações do usuário.
> Abaixo, segue um descritivo de atributos do objeto Setttings.

| ATRIBUTO     | DESCRIÇÃO            | DOMÍNIO  |
| ------------ | -------------------- | -------- |
| merchantCode | Código do cliente    | `String` |
| logicNumber  | Número lógico da LIO | `String` |

## Impressão

A Cielo LIO permite que aplicações utilizem o método de impressão por imagem para imprimir dados importantes ou necessários para o negócio do cliente. Para realizar estas ações, basta utilizar o método listado abaixo.

```java
PrinterManager printerManager = new PrinterManager(context);
```

Você deverá utilizar o seguinte callback como parâmetro de método para receber os estados relacionados à impressão.

> PrinterListener: Um callback que informa sobre todas as ações tomadas durante o processo de impressão.
> As seguintes ações pode ser notificadas:
> • onSuccess - Quando uma impressão é realizada com sucesso.
> • onError - Quando acontece um erro na impressão.
> • onWithoutPaper - Quando não há papel suficiente para realizar a impressão.

```java
PrinterListerner printerListener = new PrinterListener() {
    @Override public void onPrintSuccess(int printedLines) {
        Log.d(TAG, "onPrintSuccess");
    }

    @Override public void onError(@Nullable Throwable e) {
        Log.d(TAG, "onError");
    }

    @Override public void onWithoutPaper() {
        Log.d(TAG,"onWithoutPaper");
    }
});
```

> **Atenção:** para alterações na view do aplicativo é necessário rodar o código do callback dentro de um bloco runOnUiThread.

```java
    @Override public void onPrintSuccess(int printedLines) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showDialog();
            }
        });
    }
```

### Mapa de Estilos de Impressão

Você pode formatar a sua impressão criando mapas de estilos utilizando os parâmetros disponíveis:

| Atributo                             | Descrição                                                                                                     | Valores                                                                                                     |
| ------------------------------------ | ------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------- |
| `PrinterAttributes.KEY_ALIGN`        | Alinhamento da impressão                                                                                      | `PrinterAttributes.VAL_ALIGN_CENTER` `PrinterAttributes.VAL_ALIGN_LEFT` `PrinterAttributes.VAL_ALIGN_RIGHT` |
| `PrinterAttributes.KEY_TEXTSIZE`     | Tamanho do texto                                                                                              | Valores inteiros                                                                                            |
| `PrinterAttributes.KEY_TYPEFACE`     | Fonte do texto                                                                                                | Trabalha com um inteiro de 0 a 8, onde cada um é uma fonte diferente.                                       |
| `PrinterAttributes.KEY_MARGINLEFT`   | Margem esquerda                                                                                               | Valores inteiros                                                                                            |
| `PrinterAttributes.KEY_MARGINRIGHT`  | Margem direia                                                                                                 | Valores inteiros                                                                                            |
| `PrinterAttributes.KEY_MARGINTOP`    | Margem superior                                                                                               | Valores inteiros                                                                                            |
| `PrinterAttributes.KEY_MARGINBOTTOM` | Margem inferior                                                                                               | Valores inteiros                                                                                            |
| `PrinterAttributes.KEY_LINESPACE`    | Espaçamento entre as linhas                                                                                   | Valores inteiros                                                                                            |
| `PrinterAttributes.KEY_WEIGHT`       | Varíavel utilizada quando se trbaalho com impressão de múltiplas colunas, para escolher o peso de cada coluna | Valores inteiros                                                                                            |

Exemplo de mapas de estilos:

```java
HashMap<String, Integer> alignLeft =  new HashMap<>();
alignLeft.put(PrinterAttributes.KEY_ALIGN, PrinterAttributes.VAL_ALIGN_LEFT);
alignLeft.put(PrinterAttributes.KEY_TYPEFACE, 0);
alignLeft.put(PrinterAttributes.KEY_TEXT_SIZE, 20);

HashMap<String, Integer> alignCenter =  new HashMap<>();
alignCenter.put(PrinterAttributes.KEY_ALIGN, PrinterAttributes.VAL_ALIGN_CENTER);
alignCenter.put(PrinterAttributes.KEY_TYPEFACE, 1);
alignCenter.put(PrinterAttributes.KEY_TEXT_SIZE, 20);

HashMap<String, Integer> alignRight =  new HashMap<>();
alignRight.put(PrinterAttributes.KEY_ALIGN, PrinterAttributes.VAL_ALIGN_RIGHT);
alignRight.put(PrinterAttributes.KEY_TYPEFACE, 2);
alignRight.put(PrinterAttributes.KEY_TEXT_SIZE, 20);
```

### Impressão de imagem

Para imprimir imagens utilize o método `printImage()` do PrinterManager.
O método recebe como parâmetro o `bitmap` a ser impresso, um mapa de estilos de impressão e um listener para tratar o retorno da impressão.

```java
Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cielo);
printerManager.printImage(bitmap, alignCenter, printerListener);
```

| Imagem a ser impressa                                                                              | Resultado final                                                                                          |
| -------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------- |
| ![Impressão Imagem]({{ site.baseurl_root }}/images/cielo-lio/cielo.png){:height="50%" width="50%"} | ![Impressão Imagem]({{ site.baseurl_root }}/images/cielo-lio/print-image.png){:height="25%" width="25%"} |

| Atributo        | Descrição                    | Domínio                           |
| --------------- | ---------------------------- | --------------------------------- |
| bitmap          | Imagem a ser impressa.       | `Bitmap`                          |
| style           | Mapa de estilos de impressão | `Map<String, Int>`                |
| printerListener | Callback de impressão.       | `cielo.sdk.order.PrinterListener` |

---

## Fluxo para Integração

![fluxo para integração](https://desenvolvedores.cielo.com.br/api-portal/sites/default/files/fluxogram-local-new.png)

1. Leia toda a documentação sobre a Integração Local.
2. Crie sua conta no Portal de Desenvolvedores da Cielo para obter todas as funcionalidades que o portal pode oferecer para você, desenvolvedor. É obrigatório possuir uma conta para gerar um Client-Id.
3. Realizando o cadastro de Client ID, o desenvolvedor recebe os tokens
4. (Client ID e Access-Token) e consegue utilizar o Cielo LIO Order Manager SDK. Realize os testes no Emulador Cielo Lio Confira o link: [Download do Emulador](https://s3-sa-east-1.amazonaws.com/cielo-lio-emulator/latest-release/lio-emulator.apk)
5. Acesse a [Cielo Store](https://www.cieloliostore.com.br/login) e faça o upload do APK da sua aplicação. Ao término do upload, submeta seu aplicativo para certificação e preencha todas as informações necessárias.
6. Ao término do upload submeta seu aplicativo, no próprio ambiente da Cielo Store, para certificação e preencha todas as informações necessárias.
7. Assim que a certificação for concluída, o desenvolvedor receberá um e-mail e deverá acessar novamente a Cielo Store para promover o aplicativo para produção.
8. Agora seu aplicativo já está disponível para download na Cielo Store. Sucesso!! Aproveite ao máximo tudo que a Integração Local Cielo LIO pode oferecer para você!!

## Release Notes

Na tabela abaixo, é possível verificar as funcionalidades do SDK disponíveis por versão da Cielo LIO e Cielo Mobile.

| Funcionalidade                                                                                                                                                                                                                    | Versão Cielo LIO | Versão Cielo Mobile |
| --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------- | ------------------- |
| createDraftOrder(String reference); placeOrder(Order order); updateOrder(Order order); checkoutOrder(String orderId, PaymentListener paymentListener); checkoutOrder(String orderId,long value, PaymentListener paymentListener); | 1.10.2           | 1.9.1               |
| retrievePaymentType(); checkoutOrder(String orderId,long value, String primaryCode, String secondaryCode, PaymentListener,paymentListener);                                                                                       | 1.12.0           | 1.10.3              |
| retrieveOrders(int pageSize, int page);                                                                                                                                                                                           | 1.13.0           | 1.10.5              |
| checkoutOrder (String orderId,long value, String primaryCode, String secondaryCode, Long installments, PaymentListener paymentListener);                                                                                          | 1.14.0           | 1.12.1              |
| cancelOrder (Context context, String orderId, Payment payment, CancellationListener paymentListener); cancelOrder(Context context, String orderId, Payment payment, Long amount, CancellationListener paymentListener);           | 1.16.7           | 1.12.10             |

> **Atenção:** Se for utilizada uma funcionalidade que não esteja disponível, será lançada uma exceção – UnsupportedOperationExcepetion –, que deve ser tratada pelo aplicativo.

Para descobrir a versão do aplicativo Cielo LIO (versão do Launcher) e Cielo Mobile, bem como utilizar todas as funcionalidades do Cielo LIO Order Manager SDK, acesse “Sobre a Cielo LIO” (Ajuda -> Sobre a Cielo LIO) para obter as informações sobre os aplicativos instalados na Cielo LIO.

---

## Code Sample

O código do exemplo do aplicativo integrado com o Cielo LIO Order Manager SDK encontra-se disponível no [GitHub](https://github.com/DeveloperCielo/LIO-SDK-Sample-Integracao-Local).

## Vídeo Demo

No vídeo abaixo, é possível ver um exemplo de aplicativo integrado com o Cielo LIO Order Manager SDK, utilizando a função de pagamento de valores do SDK.

[![Vídeo Demo](https://i.imgur.com/uDx1ev3.png)](https://www.youtube.com/watch?v=d7y-x6p36YU){:target="\_blank"}

Clique na imagem acima, para acessar o vídeo
