# Integração Local - SDK v0.17.11

## Apresentação

O objetivo da Integração Local da Cielo LIO é permitir que o aplicativo desenvolvido em Android se integre com o módulo de pedidos e pagamentos da Cielo LIO através do Cielo LIO Order Manager SDK.

Nesse modelo de integração, toda a gestão do estabelecimento comercial e da arquitetura da solução fica sob responsabilidade do aplicativo que irá utilizar o SDK nas operações de pagamento.

* O aplicativo do parceiro rodando na Cielo envia informações para o Cielo LIO Order Manager SDK.
* O Cielo LIO Order Manager SDK executa os fluxos para pagamento.
* O aplicativo do parceiro recebe as informações do pagamento e continua sua execução.

As seções abaixo possuem todas as informações necessárias para realizar a integração de forma rápida e segura.
Caso queira entender melhor todo o ambiente de desenvolvimento Cielo LIO, acesso nossa [documentação completa](https://developercielo.github.io/manual/cielo-lio)

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

Faça o download do .apk do emulador no link abaixo:

[Download do Emulador](https://s3-sa-east-1.amazonaws.com/cielo-lio-store/apps/lio-emulator/1.0.0/lio-emulator.apk)

Para instalar o aplicativo, basta seguir os seguintes passos:

* Acesse as configurações do seu aparelho e vá até o menu “Segurança”;
* Localize o item “Fontes desconhecidas”, na seção “Administração do dispositivo”
* Toque na chave ao lado e confirme sua escolha para passar a permitir a instalação de arquivos APK baixados por fontes alternativas.

### Configurar módulos e dependências

Após configurar um novo projeto no Android Studio, é necessário incluir a dependência abaixo no projeto.

Para a versão release, adicione o módulo do Cielo LIO Order Manager SDK nas dependências do Gradle:

```
compile 'com.cielo.lio:order-manager:0.17.11
```

> A partir da versão 0.17.7-beta do SDK se tornou necessária a permissão de INTERNET na aplicação cliente.
Adicione essa permissão ao `AndroidManifest.xml` da sua aplicação.

### Credenciais

Para utilizar o Cielo LIO Order Manager SDK, é necessário inserir as seguintes credenciais na inicialização do OrderManager:

```
Credentials credentials = new Credentials("Seu client id aqui", "Seu accessToken aqui");
```

* Client-Id
Identificação de acesso. Sua geração ocorre no momento da criação pelo painel do desenvolvedor. Seu valor pode ser visualizado na coluna Client ID, dentro do menu 'Desenvolvedor' -> 'Client ID Cadastrados'.

* Access-Token
Identificação do token de acesso, que armazena as regras de acesso permitidas ao Client ID. Sua geração ocorre no momento da criação do Client ID pelo painel do desenvolvedor. Seu valor pode ser visualizado clicando em 'detalhes' na coluna 'Access Tokens', dentro do menu 'Desenvolvedor' -> 'Client ID Cadastrados'.

### Utilização

O fluxo básico para utilização do SDK pode ser dividido em 7 etapas, conforme o diagrama abaixo:

![order manager sdk fluxo](https://desenvolvedores.cielo.com.br/api-portal/sites/default/files/fluxogram-lio-sdk.png)

Abaixo, iremos mostrar como realizar cada uma dessas etapas.

#### Criar OrderManager

Este método permite iniciar o OrderManager, que é responsável pelas principais operações do Cielo LIO Order Manager SDK. O OrderManager representa a interface com a API REST do Order Manager.

Tudo começa com a criação do OrderManager!

```
OrderManager orderManager = new OrderManager(credentials, context);
```

O construtor do OrderManager recebe 2 parâmetros:

| Atributo    | Descrição                                                                                                                                                                                                                                                                                              | Domínio                     |
|-------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------|
| credentials | Credenciais de acesso do Parceiro/Desenvolvedor. Depois de se registrar no Portal de Desenvolvedores e criar seu app, o Parceiro/Desenvolvedor recebe as credenciais (Client-Id e Access-Token). Elas servem para identificá-lo unicamente na plataforma LIO e diferenciar as ordens da sua aplicação. | cielo.sdk.order.Credentials |
| context     | O contexto da aplicação                                                                                                                                                                                                                                                                                | android.content.Context     |

>**Atenção:** Recomendamos criar/inicializar o OrderManager no aplicativo do parceiro no momento em que for utilizar as funcionalidades, visando otimizar o desempenho do aplicativo

#### Vincular o contexto da aplicação ao SDK

Com o método `bind()`, é possível vincular o contexto da aplicação ao SDK. Este serviço é responsável por gerenciar as funções relacionadas com as ordens da LIO

```
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

>**Atenção:** Você deve garantir que o listener `onServiceBound()` foi chamado antes de utilizar as funções do OrderManager, caso contrário uma exceção será lançada e causará um crash na sua aplicação.

O método Bind recebe dois parâmetros:

* **context:** objeto que proverá o contexto em que o serviço será vinculado.

* **serviceBindListener:** listener que notifica o estado da conexão com o serviço OrderManager.

Com o OrderManager inicializado e após a execução do método onServiceBound, se torna possível ir para a próxima etapa e criar um pedido (classe Order).

#### Criar um pedido

A Cielo LIO trabalha com o conceito de Order (pedido). É necessário possuir um pedido para então realizar o(s) pagamento(s).

Este método permite criar uma Order (classe Order). Para realizar essa criação, utilize o método createDraftOrder, que disponibilizará uma Order com status DRAFT:

```
Order order = orderManager.createDraftOrder("REFERÊNCIA_DO_PEDIDO");
```

#### Adicionar itens em um pedido

Este método permite que sejam adicionados itens em um pedido.

> **Atenção:** É necessário adicionar no mínimo um item a um pedido para que seja possível dar continuidade ao pagamento.

```
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

#### Liberar pedido para pagamento

Este método permite atualizar o status de um pedido e liberá-lo para pagamento. O objetivo é utilizar este método depois de adicionar todos os itens no pedido.

```
orderManager.placeOrder(order);
```

Após a execução desse método, o status da Order irá trocar de DRAFT para ENTERED, permitindo que a mesma seja paga.

> **Atenção:** É importante observar que, uma vez que uma Order tenha mudado de status para ENTERED, não será mais possível incluir itens na mesma.
Iniciar processo de pagamento

Este método permite iniciar o processo de pagamento na Cielo LIO.

## Processo de Pagamento

Existem 4 formas diferentes de chamar o método checkoutOrder:

![checkoutorder formas](https://desenvolvedores.cielo.com.br/api-portal/sites/default/files/checkoutOrder.jpg)

Independende da forma escolhida, você deverá utilizar o seguinte callback como parâmetro do método de `checkoutOrder()` para receber os estados relacionados ao pagamento.

> PaymentListener: Um callback que informa sobre todas as ações tomadas durante o processo de pagamento. 
As seguintes ações podem ser notificadas:  
• onStart - Quando se dá o início do pagamento. 
• onPayment - Quando um pagamento é realizado. Notem que um pedido pode ser pago por mais de um pagamento. 
• onCancel - Quando o pagamento é cancelado. 
• onError - Quando acontece um erro no pagamento do pedido.

```
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

O método `onPayment()` retorna um objeto `Order` com uma lista de `Payment` que possui um HashMap `PaymentFields` com a maioria dos dados da transação.
Segue abaixo a tabela com os dados mais relevantes existentes nesse mapa:

| Nome do Campo            | Descrição do Campo                                                 | Valor Exemplo                            |
|--------------------------|--------------------------------------------------------------------|------------------------------------------|
| clientName               | Nome do Portador                                                   | VISA ACQUIRER TEST CARD 03               |
| hasPassword              | Validar se a operação pediu senha                                  | true                                     |
| primaryProductCode       | Código do produto primário                                         | 4                                        |
| primaryProductName       | Nome do produto primário                                           | CREDITO                                  |
| upFrontAmount            | Valor da entrada da transação                                      | 2500                                     |
| creditAdminTax           | Valor da taxa de administração de crédito                          | 0                                        |
| firstQuotaDate           | Data de débito da primeira parcela                                 | 25/12/2018 (dd/MM/yyy)                   |
| externalCallMerchantCode | Número do Estabelecimento Comercial                                | 0010000244470001                         |
| hasSignature             | Validar se a operação pediu assinatura                             | false                                    |
| hasPrintedClientReceipt  | Validar se imprimiu a via do cliente                               | false                                    |
| applicationName          | Pacote da aplicação                                                | cielo.launcher                           |
| interestAmount           | Valor de juros                                                     | 5000                                     |
| changeAmount             | Valor de troco                                                     | 4500                                     |
| serviceTax               | Taxa de serviço                                                    | 2000                                     |
| cityState                | Cidade - Estado                                                    | Barueri - SP                             |
| v40Code                  | Tipo da transação                                                  | 5 (Lista de Tipos de transação)   |
| secondaryProductName     | Nome do produto secundario                                         | PARC. ADM                                |
| paymentTransactionId     | ID da transação de pagamento                                       | 98437.29102507174                        |
| pan                      | Número cartão tokenizado (6 primeiros dígitos – 4 últimos dígitos) | 476173-0036                              |
| originalTransactionId    | ID da transação original, nos casos de cancelamento                | “0”                                      |
| cardLabelApplication     | Tipo de aplicação utilizada pelo cartão na transação               | CREDITO DE VISA                          |
| secondaryProductCode     | Código do produto secundário                                       | 205                                      |
| documentType             | (J) = Pessoa Jurídica (F) = Pessoa Física                          | J                                        |
| statusCode               | Status da transação1 - Autorizada 2 - Cancelada                    | 1                                        |
| merchantAddress          | Endereço do estabelecimento comercial (lojista)                    | Alameda Grajau, 219                      |
| merchantCode             | Número do Estabelecimento Comercial                                | 0010000244470001                         |
| hasConnectivity          | Valida se a transação foi online                                   | true                                     |
| productName              | forma de pagamento compilada                                       | CREDITO PARCELADO ADM - I                |
| merchantName             | Nome Fantasia do Estabelecimento Comercial                         | LOJA ON                                  |
| firstQuotaAmout          | Valor da primeira parcela                                          | 0                                        |
| cardCaptureType          | Código do tipo de captura do cartão                                | 0 (Lista de Tipos de Captura) |
| requestDate              | Data da requisição em milisegundos                                 | 1293857600000                            |
| boardingTax              | Taxa de embarque                                                   | 1200                                     |
| applicationId            | Pacote de aplicação                                                | cielo.launcher                           |
| numberOfQuotas           | Número de parcelas                                                 | 2                                        |

> Todos os valores financeiros são informados sem vírgula, ou seja 2500 são equivalentes a R$ 25,00. Os outros objetos possuem documentação própria que 
pode ser visualizada pelo Android Studio quando a classe é declarada.

| Lista de Tipos de Captura |                                  |
|---------------------------|----------------------------------|
| 0                         | Transação com cartão de chip     |
| 1                         | Transação digitada               |
| 2                         | Transação com cartão de tarja    |
| 3                         | Transação com cartão contactless |

| Lista de Tipos de Transação |                                  |
|-----------------------------|----------------------------------|
| 4                           | Crédito à vista                  |
| 5                           | Crédito parcelado administrativo |
| 6                           | Crédito parcelado loja           |
| 7                           | Pré autorização                  |
| 8                           | Débito à vista                   |
| 9                           | Débito pré datado                |
| 10                          | Crediário venda                  |
| 11                          | Crediário simulação              |
| 13                          | Voucher alimentação/refeição     |
| 28                          | Cancelamento de venda            |

### 1. Pagamento Parcial
No Pagamento parcial, o valor do pagamento é informado dentro do fluxo de telas da Cielo LIO. Na sequência, o fluxo de pagamento da Cielo LIO é iniciado (definir o valor a ser pago, escolher a forma de pagamento, inserir o cartão, digitar a senha e visualizar e/ou enviar por e-mail o comprovante).

```orderManager.checkoutOrder(orderId, paymentListener);```

Nessa forma de pagamento, é necessário apenas fazer a chamada do método enviando os seguintes parâmetros:

| Atributo        | Descrição  | Domínio|
|-----------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------|
| orderId         | O identificador do pedido a ser pago. | String   |
| paymentListener | Callback de pagamento. | cielo.sdk.order.payment.PaymentListener |

**Fluxo da transação utilizando o pagamento parcial da Cielo LIO**

Abaixo, segue um exemplo do fluxo com as telas exibidas durante o pagamento parcial:
![fluxo parcial](https://desenvolvedores.cielo.com.br/api-portal/sites/default/files/pagamento-parcial.jpg)

### 2. Pagamento de valor
No pagamento de Valores, o valor a ser pago é informado pelo aplicativo e a forma de pagamento (crédito, débito, parcelado) é escolhida dentro do fluxo de telas da Cielo LIO.

```orderManager.checkoutOrder(orderId, value, paymentListener);```

Nessa forma de pagamento é possível enviar qualquer valor a ser pago (valor total ou valor parcial do pedido), tudo depende do modelo de negócio adotado pelo aplicativo do parceiro. Para fazer a chamada é necessário enviar os seguintes parâmetros:

| Atributo        | Descrição  | Domínio|
|-----------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------|
| orderId         | O identificador do pedido a ser pago. | String   |
| value         | Valor a ser pago. R$ 10,00 deve ser enviado como 1000. | Long   |
| paymentListener | Callback de pagamento. | cielo.sdk.order.payment.PaymentListener |

**Fluxo da transação utilizando o pagamento de valor da Cielo LIO**

Abaixo, segue um exemplo do fluxo com as telas exibidas durante o pagamento de valor:
![fluxo parcial](https://desenvolvedores.cielo.com.br/api-portal/sites/default/files/pagamento-valor.jpg)

### 3. Pagamento direto
No Pagamento Direto, o valor a ser pago e a forma de pagamento (crédito, débito, parcelado) será definido dentro da aplicação do parceiro. Na sequência o fluxo de pagamento da Cielo LIO é iniciado (inserir o cartão, digitar a senha e visualizar e/ou enviar por e-mail comprovante).

Para realizar o pagamento direto é necessário, primeiramente, verificar os tipos de pagamento habilitados para Cielo LIO do estabelecimento comercial utilizando a função de consulta de formas de pagamentos habilitadas para LIO:

```ArrayList<PrimaryProduct> paymentTypes = orderManager.retrievePaymentType();```

Esta função disponibilizará uma lista de objetos do tipo PrimaryProduct onde será possível recuperar os códigos de pagamento habilitados.

Os seguintes campos serão retornados por essa função:

| Atributo        | Descrição  | Domínio|
|-----------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------|
| id         |identificador do código no sistema. | Long   |
| code         | utilizado na função de pagamento. | String   |
| name         | Nome do método de pagamento para efeitos de exibição. | String   |
| secondaryProducts         | Listagem de códigos de pagamento secundário. ex.: À Vista, Parcelado, etc. | ArrayList<SecondaryProduct>   |

Com os códigos de pagamentos disponíveis para a Cielo LIO do estabelecimento comercial, basta realizar a chamada de Pagamento Direto informando os códigos dos produtos:

```
  PrimaryProduct primaryProduct = paymentTypes.get(0);
  SecondaryProduct secondaryProduct = primaryProduct.getSecondaryProducts().get(0);
  String primaryCode = primaryProduct.getCode();
  String secondaryCode = secondaryProduct.getCode();
  orderManager.checkoutOrder(orderId, primaryCode, secondaryCode paymentListener);
```

Nessa forma de pagamento é possível enviar qualquer valor a ser pago e a forma de pagamento. Para fazer a chamada é necessário enviar os seguintes parâmetros:

| Atributo        | Descrição  | Domínio|
|-----------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------|
| orderId         | O identificador do pedido a ser pago. | String   |
| value         | Valor a ser pago. R$ 10,00 deve ser enviado como 1000. | Long   |
| primaryCode         | Código identificador do método primário de pagamento ex.: Débito, Crédito. | String   |
| secondaryCode         | Código identificador do método secundário de pagamento ex.: À vista, Parcelado. | String   |
| paymentListener | Callback de pagamento. | cielo.sdk.order.payment.PaymentListener |

**Fluxo da transação utilizando o pagamento direto da Cielo LIO**

```orderManager.checkoutOrder(orderId, value, primaryCode, secondaryCode, paymentListener);```

Abaixo, segue um exemplo do fluxo com as telas exibidas durante o pagamento de valor:
![fluxo parcial](https://desenvolvedores.cielo.com.br/api-portal/sites/default/files/pagamento-direto.jpg)

### 4. Pagamento parcelado
Para efetuar um pagamento parcelado, é preciso consultar as formas de pagamento utilizando a função disponibilizada no SDK, escolher crédito no produto primário e crédito no produto secundário **obrigatoriamente** e informar o número de parcelas no momento do checkout. É importante lembrar que se os códigos de pagamento forem informados incorretamente ou se o número de parcelas não for maior do que **0**, o sistema acusará erro.
Nessa forma de pagamento, é necessário apenas fazer a chamada do método enviando os seguintes parâmetros:

| Atributo        | Descrição  | Domínio|
|-----------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------|
| orderId         | O identificador do pedido a ser pago. | String   |
| value         | Valor a ser pago. R$ 10,00 deve ser enviado como 1000. | Long   |
| primaryCode         | Código identificador do método primário de pagamento ex.: Débito, Crédito. | String   |
| secondaryCode | Código identificador do método secundário de pagamento ex.: À vista, Parcelado. | String   |
| installments | Número de parcelas. | Long   |
| paymentListener | Callback de pagamento. | cielo.sdk.order.payment.PaymentListener |

**Fluxo da transação utilizando o pagamento parcelado da Cielo LIO**

```orderManager.checkoutOrder(orderId, value, primaryCode, secondaryCode, installments,  paymentListener);```

Abaixo, segue um exemplo do fluxo com as telas exibidas durante o pagamento parcelado:
![fluxo parcial](https://desenvolvedores.cielo.com.br/api-portal/sites/default/files/pagamento-parcelado.jpg)

### 5. Pagamento informando email
Exatamente como o método anterior, com a diferença que neste caso é possível informar o email para facilitar o preenchimento na hora de enviar o comprovante para o cliente. Caso queira utilizar essa função para realizar outro tipo de pagamento que não o parcelado, basta informar os códigos primários desejados e informar **0** no número de parcelas.
Nessa forma de pagamento, é necessário apenas fazer a chamada do método enviando os seguintes parâmetros:

| Atributo        | Descrição  | Domínio|
|-----------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------|
| orderId         | O identificador do pedido a ser pago. | String   |
| value         | Valor a ser pago. R$ 10,00 deve ser enviado como 1000. | Long   |
| primaryCode         | Código identificador do método primário de pagamento ex.: Débito, Crédito. | String   |
| secondaryCode         | Código identificador do método secundário de pagamento ex.: À vista, Parcelado. | String   |
| installments | Número de parcelas. | Long   |
| paymentListener | Callback de pagamento | cielo.sdk.order.payment.PaymentListener |

**Fluxo da transação utilizando o pagamento direto da Cielo LIO**

```orderManager.checkoutOrder(orderId, value, primaryCode, secondaryCode, installments,  paymentListener);```

Abaixo, segue um exemplo do fluxo com as telas exibidas durante o pagamento com email:
![fluxo parcial](https://desenvolvedores.cielo.com.br/api-portal/sites/default/files/pagamento-parcelado.jpg)

## Cancelamento

Existem 2 formas de cancelar um pagamento na Cielo LIO:

Cancelar Pagamento Total
Cancelar Parte do Valor do Pagamento

Independende da forma escolhida, você deverá utilizar o seguinte callback como parâmetro do método de `cancelOrder()` para receber os estados relacionados ao cancelamento: 

```

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
> CancellationListener: Um callback que informa sobre todas as ações tomadas durante o processo de cancelamento. 
As seguintes ações pode ser notificadas: 
• onSuccess - Quando um cancelamento é realizado com sucesso. 
• onCancel - Quando o usuário cancela a operação. 
• onError - Quando acontece um erro no cancelamento do pedido.

### Cancelar Pagamento Total

No método Cancelar um Pagamento, é necessário ter salvo uma instância da Order que contém as informações da Order. Essa Order pode ser recuperada no sucesso do callback do pagamento ou usando o método de Listagem de Pedidos (Orders) (link para método). 
Assim que possuir a instância da Order, utilize o método abaixo passando os parâmetros conforme o exemplo abaixo:

```orderManager.cancelOrder(context, orderId, payment, cancellationListener);```

Abaixo é detalhado cada um dos parâmetros enviados no método:

| Atributo        | Descrição  | Domínio|
|-----------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------|
| context         | Contexto da sua aplicação. | String   |
| orderID         | O identificador do pedido a ser pago. | Long   |
| payment         | O pagamento a ser cancelado. | cielo.sdk.order.payment.Payment
| cancellationListener | Callback que informa sobre todas as ações tomadas durante o processo de cancelamento. | cielo.sdk.order.payment.CancellationListener |

### Cancelar parte do valor de um pagamento

No método Cancelar parte do valor de um Pagamento, é necessário ter salvo uma instância da Order que contém as informações da Order. Essa Order pode ser recuperada no sucesso do callback do pagamento ou usando o método de Listagem de Pedidos (Orders). 
Os parâmetros do método anterior, com a inclusão de um parâmetro que é o valor a ser cancelado. 

>Nesse cenário é preciso ter atenção para não passar um valor maior do que o do pagamento, caso contrário o sistema retornará um erro.

Portanto, assim que possuir a instância da Order, utilize o método abaixo passando os parâmetros conforme o exemplo abaixo:

```orderManager.cancelOrder(context, orderId, payment, value, cancellationListener);```

Abaixo é detalhado cada um dos parâmetros enviados no método:

| Atributo        | Descrição  | Domínio|
|-----------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------|
| context         | Contexto da sua aplicação. | String   |
| orderID         | O identificador do pedido a ser pago. | Long   |
| payment         | O pagamento a ser cancelado. | cielo.sdk.order.payment.Payment |
| value           | O valor a ser cancelado. | Long   |
| cancellationListener | Callback que informa sobre todas as ações tomadas durante o processo de cancelamento. |  cielo.sdk.order.payment.CancellationListener |

## Informações do terminal

Todas as informações referentes ao terminal, que foram expostas, estão disponíveis no InfoManager

```InfoManager infoManager = new InfoManager();```

### Nível de Bateria

Para consultar o nível de carga da LIO, basta utilizar o métdo abaixo:

```
infoManager.getBatteryLevel(context);
```
    
> O valor da bateria será retornado em uma `String` contendo um valor de 0 a 100 em caso de sucesso.  
Em caso de erro a `String` "---" será retornada. 

### Verificar Modelo da LIO

O SDK disponibiliza um método para verificar se seu aplicativo está instalado numa LIO V1 ou V2. Basta acessar da seguinte forma:

``` infoManager.getDeviceModel(); ```

O mesmo irá retornar um enum do tipo `DeviceModel` com o modelo correspondente (`LIO_V1` || `LIO_V2` ).

### Obtendo informações do usuário (EC e Número Lógico)

Através do SDK, é possível recuperar os dados do cliente e número lógico de maneira simples utilizando o método abaixo:

```
infoManager.getSettings(context);
``` 

> Esta função, retornará um objeto do tipo Settings onde é possível recuperar as informações do usuário. 
Abaixo, segue um descritivo de atributos do objeto Setttings.

| ATRIBUTO     | DESCRIÇÃO            | DOMÍNIO |
|--------------|----------------------|---------|
| merchantCode | Código do cliente    | String  |
| logicNumber  | Número lógico da LIO | String  |

## Impressão [Apenas LIO V2]

A nova versão da Cielo LIO V2 permite que aplicações parceiras utilizem os métodos disponíveis da impressora para imprimir dados importantes ou necessários para o negócio do cliente. Para realizar estas operações, basta utilizar um dos métodos listados abaixo que permite a impressão de uma única linha, uma sequência de linhas, ou imagens.

```PrinterManager printerManager = new PrinterManager(context)```

Independende da forma de impressão escolhida, você deverá utilizar o seguinte callback como parâmetro do método de para receber os estados relacionados à impressão:

> PrinterListener: Um callback que informa sobre todas as ações tomadas durante o processo de impressão. 
As seguintes ações pode ser notificadas: 
• onSuccess - Quando uma impressão é realizada com sucesso. 
• onError - Quando acontece um erro na impressão.
• onWithoutPaper - Quando não há papel suficiente para realizar a impressão.

```
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

### Mapa de Estilos de Impressão

Você pode formatar a sua impressão criando mapas de estilos utilizando os parâmetros disponíveis:

| Atributo        | Descrição | Valores  |
|-----------------|-----------|----------|
|`PrinterAttributes.KEY_ALIGN` | Alinhamento da impressão | `PrinterAttributes.VAL_ALIGN_CENTER`  `PrinterAttributes.VAL_ALIGN_LEFT` `PrinterAttributes.VAL_ALIGN_RIGHT` |
|`PrinterAttributes.KEY_TEXTSIZE` | Tamanho do texto | Valores inteiros | 
|`PrinterAttributes.KEY_TYPEFACE` | Fonte do texto | Trabalha com um inteiro de 0 a 8, onde cada um é uma fonte diferente.|
|`PrinterAttributes.KEY_MARGINLEFT` | Margem esquerda |Valores inteiros | 
|`PrinterAttributes.KEY_MARGINRIGHT` | Margem direia | Valores inteiros | 
|`PrinterAttributes.KEY_MARGINTOP` | Margem superior | Valores inteiros | 
|`PrinterAttributes.KEY_MARGINBOTTOM` | Margem inferior | Valores inteiros | 
|`PrinterAttributes.KEY_LINESPACE` | Espaçamento entre as linhas | Valores inteiros | 
|`PrinterAttributes.KEY_WEIGHT` | Varíavel utilizada quando se trbaalho com impressão de múltiplas colunas, para escolher o peso de cada coluna | Valores inteiros | 

Exemplo de mapas de estilos:

```
HashMap<String, Integer> alignLeft =  new HashMap<>();
alignLeft.put(PrinterAttributes.KEY_ALIGN, PrinterAttributes.VAL_ALIGN_LEFT);
alignLeft.put(PrinterAttributes.KEY_MARGIN_TOP, 50);
alignLeft.put(PrinterAttributes.KEY_MARGIN_BOTTOM, 50);
alignLeft.put(PrinterAttributes.KEY_TYPEFACE, 0);
alignLeft.put(PrinterAttributes.KEY_TEXT_SIZE, 20);

HashMap<String, Integer> alignCenter =  new HashMap<>();
alignCenter.put(PrinterAttributes.KEY_ALIGN, PrinterAttributes.VAL_ALIGN_CENTER);
alignCenter.put(PrinterAttributes.KEY_MARGIN_TOP, 50);
alignCenter.put(PrinterAttributes.KEY_MARGIN_BOTTOM, 50);        
alignCenter.put(PrinterAttributes.KEY_TYPEFACE, 1);
alignCenter.put(PrinterAttributes.KEY_TEXT_SIZE, 20);

HashMap<String, Integer> alignRight =  new HashMap<>();
alignRight.put(PrinterAttributes.KEY_ALIGN, PrinterAttributes.VAL_ALIGN_RIGHT);
alignRight.put(PrinterAttributes.KEY_MARGIN_TOP, 50);
alignRight.put(PrinterAttributes.KEY_MARGIN_BOTTOM, 50);
alignRight.put(PrinterAttributes.KEY_TYPEFACE, 2);
alignRight.put(PrinterAttributes.KEY_TEXT_SIZE, 20);
```
### Impressão de texto simples

Para imprimir textos simples utilize o método ```printText()``` do PrinterManager. 
O método recebe como parâmetro o texto a ser impresso, um mapa de estilo de impressão e um listener para tratar o retorno da impressão.

```
String textToPrint = "Texto simples a ser impresso.\n Com múltiplas linhas";
printerManager.printText(textToPrint, alignLeft, printerListener);
```

| Atributo | Descrição | Domínio |
|----------|-----------|---------|
| text     | Texto a ser impresso. | `String` |
| style    | Mapa de estilos de impressão | `Map<String, Int>` |
| printerListener | Callback de impressão. | cielo.sdk.order.PrinterListener |

### Impressão de múltiplas colunas

Para imprimir textos em múltiplas colunas utilize o método ```printMultipleColumnText()``` do PrinterManager. 
O método recebe como parâmetro um array de texts a serem impresso, um array com mapas de estilos de impressão, respectivamente e um listener para tratar o retorno da impressão.

```
String[] textsToPrint = new String[] { "ALIGN LEFT", "ALIGN CENTER", "ALIGN RIGHT" }

List<Map<String, Integer>> styles =  new ArrayList<>();
styles.add(alignLeft);
styles.add(alignCenter);
styles.add(alignRight);

printerManager.printMultipleColumnText(textsToPrint, styles, printerListener);
```
| Atributo | Descrição | Domínio |
|----------|-----------|---------|
| stringArray     | Lista de textos a serem impressos. | `String[]` |
| style    | Lista de mapa de estilos de impressão | `List<Map<String, Int>>` |
| printerListener | Callback de impressão. | cielo.sdk.order.PrinterListener |

### Impressão de imagem

Para imprimir imagens utilize o método ```printImage()``` do PrinterManager. 
O método recebe como parâmetro o `bitmap` a ser impresso, um mapa de estilos de impressão e um listener para tratar o retorno da impressão.

```
Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cielo);
printerManager.printImage(bitmap, alignCenter, printerListener);
```

| Atributo | Descrição | Domínio |
|----------|-----------|---------|
| bitmap     | Imagem a ser impressa. | `Bitmap` |
| style    | Mapa de estilos de impressão | `Map<String, Int>` |
| printerListener | Callback de impressão. | cielo.sdk.order.PrinterListener |

---

**Fluxo da transação utilizando o pagamento direto da Cielo LIO**

```orderManager.checkoutOrder(orderId, value, primaryCode, secondaryCode, installments,  paymentListener);```

Abaixo, segue um exemplo do fluxo com as telas exibidas durante o pagamento parcelado:
![fluxo parcial](https://desenvolvedores.cielo.com.br/api-portal/sites/default/files/pagamento-parcelado.jpg)

## Listagem de Pedidos

Na listagem de pedidos, é possível obter todas os pedidos (Orders) abertas na Cielo LIO pelo aplicativo do parceiro. Para isso, basta utilizar a função abaixo:

``` ResultOrders resultOrders = orderManager.retrieveOrders(10, 0); ```

O objeto ResultOrders contém uma lista com todas as ordens abertas assinadas com as credenciais da aplicação.

## Finalizar uso do OrderManager

Após executar todas as operações de pagamento e caso não seja necessário utilizar o objeto orderManager, utilize método unbind para desvincular o contexto e evitar problemas de integridade. 

Fique atento ao local onde o ``` unbind() ``` será executado para evitar problemas com ciclo de vida da Activity ou Fragment que foi vinculado ao serviço.
É importante lembrar que se o contexto for alterado, é preciso desvincular o serviço (ex.: troca de Activity)
Utilize o método abaixo para finalizar o uso do OrderManager:

``` orderManager.unbind(); ```

---

## Fluxo para Integração

![fluxo para integração](https://desenvolvedores.cielo.com.br/api-portal/sites/default/files/fluxogram-local-new.png)

1. Leia toda a documentação sobre a Integração Local.
2. Crie sua conta no Portal de Desenvolvedores da Cielo para obter todas as funcionalidades que o portal pode oferecer para você, desenvolvedor. É obrigatório possuir uma conta para gerar um Client-Id.
3. Realizando o cadastro de Client ID, o desenvolvedor recebe os tokens
4. (Client ID e Access-Token) e consegue utilizar o Cielo LIO Order Manager SDK. Realize os testes no Emulador Cielo Lio Confira o link: [Download do Emulador](https://s3-sa-east-1.amazonaws.com/cielo-lio-store/apps/lio-emulator/1.0.0/lio-emulator.apk)
5. Acesse a [Cielo Store](https://www.cieloliostore.com.br/login) e faça o upload do APK da sua aplicação. Ao término do upload, submeta seu aplicativo para certificação e preencha todas as informações necessárias.
6. Ao término do upload submeta seu aplicativo, no próprio ambiente da Cielo Store, para certificação e preencha todas as informações necessárias.
7. Assim que a certificação for concluída, o desenvolvedor receberá um e-mail e deverá acessar novamente a Cielo Store para promover o aplicativo para produção.
8. Agora seu aplicativo já está disponível para download na Cielo Store. Sucesso!! Aproveite ao máximo tudo que a Integração Local Cielo LIO pode oferecer para você!!
