# Vacina Prime
Gerenciador de validade e qualidade de Vacinas contra Covid19

# Instalação


É necessário ter instalado `Kotlin 1.6.10`, além do `Java 11` instalados. Em geral, apenas ao importar o projeto no IntelliJ, todas as configurações necessárias serão sugeridas. Além disso, foi utilizado o `gradle` no projeto

## Utilizando o IntelliJ

- O projeto possui já diretamente configurações prontos para execução do programa, basta importá-lo no IntelliJ.
As configurações de rodar são as seguintes:

### Produtor de vacinas
- VaccineProducer (hospital-santa-paula)
- VaccineProducer2 (hospital-vitoria-apart)
- VaccineProducer3 (hospital-santa-paula)

Os dados de cada um desses produtores podem ser encontrados nos seguintes arquivos, na pasta resources/vaccineProducer
- producer1.json
- producer2.json
- producer3.json

Ao rodar o programa, o nome do arquivo deve ser enviado como primeiro argumento, seguido de um boolean (true ou false) indicando se a produção será fora dos limites das vacinas ou não

### Produtor dos gerentes

- ManagerProducer (Ryan)
- ManagerProducer (Matheus)

Os dados de cada um desses produtores podem ser encontrados nos seguintes arquivos, na pasta resources/managerProducer
- manager1.json
- manager2.json

Ao rodar o programa, o nome do arquivo deve ser enviado como primeiro argumento


### Consumidor de Vacina e de Managers
Basta rodar a seguinte configuração:

- Consumer Threads


Os dados de cada um dos consumidores pode ser modificado no arquivo na pasta VaccineConsumer

- consumers.json


### Consumidor de Notificações
Basta rodar a configuração `NotificationConsumer`

## Variáveis Ambiente

Para a utilização do Twilio, é necessário informar as chaves da conta, definindo as seguintes variáveis:
TWILIO_ACCOUNT_SID=sua_chave_aqui;
TWILIO_AUTH_TOKEN=sua_chave_aqui

