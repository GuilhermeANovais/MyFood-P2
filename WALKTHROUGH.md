# MyFood — Guia de Uso

## Pré-requisitos

### Versão do JDK
- **JDK 11 ou superior** é necessário
- O projeto foi testado com OpenJDK 26

**### Verificar instalação do Java
```bash
java -version
javac -version
```**

### Estrutura necessária na raiz do projeto
```
MyFood-P2/
├── easyaccept.jar        # Framework de testes de aceitação
├── lib/                  # Pasta com bibliotecas
├── data/                  # Pasta com dados JSON (criada automaticamente)
│   ├── usuarios.json
│   ├── empresas.json
│   ├── produtos.json
│   └── pedidos.json
├── src/                   # Código-fonte
├── tests/                 # Scripts de teste EasyAccept
└── run_tests.sh           # Script para rodar todos os testes
```

## Como Compilar

### Compilação manual
```bash
# Criar diretório de saída
mkdir -p out

# Compilar todos os arquivos Java
javac -d out -cp "lib/easyaccept.jar" @sources.txt
```

### O que o script run_tests.sh faz
1. Cria o diretório `out/`
2. Encontra todos os arquivos `.java` em `src/`
3. Compila-os para o diretório `out/`
4. Executa cada teste de aceitação sequencialmente
5. Exibe PASSOU ou FALHOU para cada teste

### Dar permissão de execução ao script (Linux/Mac)
```bash
chmod +x run_tests.sh
```

## Como Rodar os Testes de Aceitação

### Rodar um teste individual
```bash
# Windows
java -cp "out;lib/easyaccept.jar" easyaccept.EasyAccept facade.Facade tests/us1_1.txt

# Linux/Mac
java -cp "out:lib/easyaccept.jar" easyaccept.EasyAccept facade.Facade tests/us1_1.txt
```

### Rodar todos os testes de uma vez
```bash
./run_tests.sh
```

Ou manualmente:
```bash
for test in tests/us1_1.txt tests/us1_2.txt tests/us2_1.txt tests/us2_2.txt tests/us3_1.txt tests/us3_2.txt tests/us4_1.txt tests/us4_2.txt; do
    java -cp "out;lib/easyaccept.jar" easyaccept.EasyAccept facade.Facade $test
done
```

### Interpretar a saída
- **PASSOU**: Todos os testes do arquivo passaram
- **FALHOU**: Algum teste falhou - verifique a mensagem de erro
- **"X tests OK"**: Indica quantos testes individuais passaram

### Arquivos de teste
| Teste | Descrição |
|-------|-----------|
| `us1_1.txt` | Criação de usuários (sem persistência) |
| `us1_2.txt` | Criação de usuários (com persistência) |
| `us2_1.txt` | Criação de empresas (sem persistência) |
| `us2_2.txt` | Criação de empresas (com persistência) |
| `us3_1.txt` | Criação de produtos (sem persistência) |
| `us3_2.txt` | Criação de produtos (com persistência) |
| `us4_1.txt` | Criação de pedidos (sem persistência) |
| `us4_2.txt` | Criação de pedidos (com persistência) |

## Fluxo Completo de Uso — Passo a Passo

### 1. Criando usuários

#### Criar um Cliente (sem CPF)
```bash
# Via EasyAccept (teste)
criarUsuario nome="Carlos" email="carlos@email.com" senha="123senha" endereco="Rua Exemplo N 123"
```

#### Criar um Dono de Empresa (com CPF)
```bash
# O CPF deve ter exatamente 14 caracteres
criarUsuario nome="João" email="joao@email.com" senha="senha123" endereco="Av. Principal N 456" cpf="123.456.789-00"
```

#### Regras para criação de usuários
| Campo | Regra |
|-------|-------|
| `nome` | Não pode ser vazio |
| `email` | Deve conter `@` e ser único no sistema |
| `senha` | Não pode ser vazia |
| `endereco` | Não pode ser vazio |
| `cpf` | Exatamente 14 caracteres (para Dono de Empresa) |

#### Fazer login e capturar o ID
```bash
# Retorna o ID do usuário se email e senha estiverem corretos
id1=login email="carlos@email.com" senha="123senha"

# Consultar atributos do usuário
expect "Carlos" getAtributoUsuario id=${id1} atributo="nome"
expect "carlos@email.com" getAtributoUsuario id=${id1} atributo="email"
```

---

### 2. Criando Empresas (Restaurantes)

**Quem pode criar:** Apenas usuários do tipo Dono de Empresa

#### Criar um restaurante
```bash
# Parâmetros: tipoEmpresa, dono, nome, endereco, tipoCozinha
eid1=criarEmpresa tipoEmpresa="restaurante" dono=${id1} nome="Pastelaria Central" endereco="Rua Segura N 987" tipoCozinha="brasileira"
```

#### Regras de nome duplicado para empresas

| Situação | Regra |
|----------|-------|
| Donos **diferentes** | Não pode ter o mesmo nome no sistema inteiro |
| Mesmo **dono**, nomes **diferentes** | Permitido (endereço pode ser igual ou diferente) |
| Mesmo **dono**, mesmo **nome** | Permitido ** apenas** se o endereço for **diferente** |
| Mesmo **dono**, mesmo **nome e endereço** | **Proibido** |

```bash
# Exemplo: mesmo dono, nome igual, endereço diferente - VÁLIDO
eid2=criarEmpresa tipoEmpresa="restaurante" dono=${id1} nome="Pastelaria Central" endereco="Rua Divertida N 123" tipoCozinha="brasileira"

# Exemplo: mesmo dono, nome e endereço iguais - INVÁLIDO
# expectError "Proibido cadastrar duas empresas com o mesmo nome e local" criarEmpresa tipoEmpresa="restaurante" dono=${id1} nome="Pastelaria Central" endereco="Rua Segura N 987" tipoCozinha="brasileira"
```

#### Listar empresas de um dono
```bash
expect "{[[Pastelaria Central, Rua Segura N 987], [Pastelaria Central, Rua Divertida N 123]]}" getEmpresasDoUsuario idDono=${id1}
```

#### Consultar atributos de uma empresa
```bash
expect "Pastelaria Central" getAtributoEmpresa empresa=${eid1} atributo="nome"
expect "Rua Segura N 987" getAtributoEmpresa empresa=${eid1} atributo="endereco"
expect "brasileira" getAtributoEmpresa empresa=${eid1} atributo="tipoCozinha"
expect "João" getAtributoEmpresa empresa=${eid1} atributo="dono"
```

#### Obter ID de empresa pelo índice
```bash
# Útil quando há múltiplas empresas com o mesmo nome
expect ${eid1} getIdEmpresa idDono=${id1} nome="Pastelaria Central" indice=0
expect ${eid2} getIdEmpresa idDono=${id1} nome="Pastelaria Central" indice=1
```

---

### 3. Criando Produtos

#### Criar um produto
```bash
# Parâmetros: empresa, nome, valor, categoria
p1=criarProduto empresa=${eid1} nome="Pastel de Queijo" valor=5.00 categoria="salgado"
p2=criarProduto empresa=${eid1} nome="Refrigerante" valor=3.50 categoria="bebida"
```

#### Regra: nome único por empresa
- O **mesmo nome** pode ser usado em **empresas diferentes**
- Mas não pode ter dois produtos com o **mesmo nome** na **mesma empresa**

```bash
# INVALIDO - Já existe "Refrigerante" na pastelaria
# expectError "Ja existe um produto com esse nome para essa empresa" criarProduto empresa=${eid1} nome="Refrigerante" valor=3.00 categoria="bebida"

# VALIDO - Mesmo nome em empresa diferente
p3=criarProduto empresa=${eid2} nome="Refrigerante" valor=3.00 categoria="bebida"
```

#### Editar um produto existente
```bash
editarProduto produto=${p2} nome="Suco de Laranja" valor=4.50 categoria="bebida"
```

#### Consultar atributos de um produto
```bash
expect "4.50" getProduto nome="Suco de Laranja" empresa=${eid1} atributo="valor"
expect "bebida" getProduto nome="Suco de Laranja" empresa=${eid1} atributo="categoria"
expect "Pastelaria Central" getProduto nome="Suco de Laranja" empresa=${eid1} atributo="empresa"
```

#### Listar todos os produtos de uma empresa
```bash
expect "{[Pastel de Queijo, Suco de Laranja]}" listarProdutos empresa=${eid1}
expect "{[]}" listarProdutos empresa=${eid3}  # Empresa sem produtos
```

---

### 4. Fazendo Pedidos

**Quem pode criar pedidos:** Apenas Clientes (não Donos de Empresa)

#### Fluxo completo: criar → adicionar → fechar

**Passo 1: Criar o pedido (estado = "aberto")**
```bash
pedido1=criarPedido cliente=${idCliente} empresa=${eid1}
```

**Passo 2: Adicionar produtos ao pedido**
```bash
adicionarProduto numero=${pedido1} produto=${p1}
adicionarProduto numero=${pedido1} produto=${p2}
adicionarProduto numero=${pedido1} produto=${p1}  # Mesmo produto várias vezes
```

**Passo 3: Fechar o pedido (estado = "preparando")**
```bash
fecharPedido numero=${pedido1}
```

#### Regra dos pedidos em aberto
- Um cliente pode ter **máximo 1 pedido em aberto** por empresa
- Após fechar o pedido, pode abrir um novo

```bash
# INVALIDO - Já existe pedido em aberto para esta empresa
# expectError "Nao e permitido ter dois pedidos em aberto para a mesma empresa" criarPedido cliente=${idCliente} empresa=${eid1}

# VALID0 - Fechou o pedido anterior, agora pode criar outro
fecharPedido numero=${pedido1}
pedido2=criarPedido cliente=${idCliente} empresa=${eid1}  # Agora funciona
```

#### Remover uma ocorrência de produto
```bash
# Remove apenas UMA ocorrência do produto (não todas)
removerProduto pedido=${pedido2} produto="Pastel de Queijo"
```

#### Consultar atributos do pedido
```bash
expect "Carlos" getPedidos pedido=${pedido1} atributo="cliente"
expect "Pastelaria Central" getPedidos pedido=${pedido1} atributo="empresa"
expect "aberto" getPedidos pedido=${pedido1} atributo="estado"
expect "{[Pastel de Queijo, Suco de Laranja, Pastel de Queijo]}" getPedidos pedido=${pedido1} atributo="produtos"
expect "14.50" getPedidos pedido=${pedido1} atributo="valor"
```

#### Obter número do pedido por índice
```bash
# Índice 0 = pedido mais antigo, índice 1 = segundo mais antigo, etc.
expect ${pedido1} getNumeroPedido cliente=${idCliente} empresa=${eid1} indice=0
expect ${pedido2} getNumeroPedido cliente=${idCliente} empresa=${eid1} indice=1
```

---

## Persistência de Dados

### Onde os dados ficam salvos
```
data/
├── usuarios.json    # Usuários do sistema
├── empresas.json    # Restaurantes/empresas
├── produtos.json    # Produtos dos restaurantes
└── pedidos.json     # Pedidos dos clientes
```

### Estrutura de um arquivo JSON (exemplo: usuarios.json)
```json
{
  "nextId": 3,
  "usuarios": [
    {"id":1,"tipo":"cliente","nome":"Carlos","email":"carlos@email.com","senha":"123senha","endereco":"Rua Exemplo N 123"},
    {"id":2,"tipo":"dono","nome":"João","email":"joao@email.com","senha":"senha123","endereco":"Av. Principal N 456","cpf":"123.456.789-00"}
  ]
}
```

### zerarSistema
```bash
zerarSistema
```
- **Apaga todos os dados** do sistema (zera todas as listas)
- Reseta todos os contadores de ID para 1
- Usado no **início** de cada teste `us*_1.txt`

### encerrarSistema
```bash
encerrarSistema
quit
```
- **Salva todos os dados** nos arquivos JSON
- Deve ser chamado **ao final** de cada teste

### Por que os testes _2 rodam sem zerarSistema
Os testes `us1_2`, `us2_2`, `us3_2`, `us4_2` **não** chamam `zerarSistema` no início, então:
1. Os dados do teste anterior permanecem em memória
2. O teste verifica se a **persistência funciona** corretamente
3. `encerrarSistema` é chamado ao final de cada teste para salvar

---

## Erros Comuns e Como Resolver

| Mensagem de Erro | Causa | Como Corrigir |
|------------------|-------|----------------|
| `Usuario nao cadastrado.` | Tentou acessar/atributos de um usuário que não existe | Verifique se o ID do usuário está correto |
| `Conta com esse email ja existe` | Email já foi cadastrado anteriormente | Use um email diferente |
| `CPF invalido` | CPF não tem exatamente 14 caracteres | Use formato: `123.456.789-00` |
| `Nome invalido` | Campo nome está vazio | Forneça um nome não vazio |
| `Email invalido` | Email não contém `@` ou está vazio | Use formato válido: `usuario@dominio.com` |
| `Senha invalido` | Campo senha está vazio | Forneça uma senha não vazia |
| `Endereco invalido` | Campo endereço está vazio | Forneça um endereço não vazio |
| `Login ou senha invalidos` | Email ou senha incorretos | Verifique as credenciais |
| `Empresa com esse nome ja existe` | Outro dono já tem empresa com esse nome | Escolha outro nome para a empresa |
| `Proibido cadastrar duas empresas com o mesmo nome e local` | Mesmo dono tentou criar empresa igual no mesmo endereço | Use endereço diferente ou outro nome |
| `Usuario nao pode criar uma empresa` | Apenas Donos de Empresa podem criar | O usuário deve ser do tipo Dono (ter CPF) |
| `Atributo invalido` | Nome de atributo desconhecido | Verifique os atributos válidos na referência |
| `Empresa nao cadastrada` | Empresa com ID informado não existe | Verifique o ID da empresa |
| `Ja existe um produto com esse nome para essa empresa` | Já existe produto com mesmo nome na empresa | Escolha outro nome para o produto |
| `Valor invalido` | Valor negativo passado | Use valores >= 0 |
| `Categoria invalido` | Categoria vazia | Forneça uma categoria não vazia |
| `Produto nao cadastrado` | Produto com ID informado não existe | Verifique o ID do produto |
| `Produto nao encontrado` | Produto com nome informado não existe na empresa | Verifique o nome do produto |
| `Dono de empresa nao pode fazer um pedido` | Dono de empresa tentou criar pedido | Apenas Clientes podem fazer pedidos |
| `Nao e permitido ter dois pedidos em aberto para a mesma empresa` | Cliente já tem pedido aberto para esta empresa | Feche o pedido aberto primeiro |
| `O produto nao pertence a essa empresa` | Produto pertence a outra empresa | Adicione apenas produtos da empresa do pedido |
| `Nao e possivel adcionar produtos a um pedido fechado` | Pedido já foi fechado (estado = "preparando") | Crie um novo pedido |
| `Nao e possivel remover produtos de um pedido fechado` | Pedido já foi fechado | Não é possível modificar pedido fechado |
| `Pedido nao encontrado` | Pedido com número informado não existe | Verifique o número do pedido |
| `Nao existe pedido em aberto` | Tentou adicionar produto a pedido inexistente | Verifique o número do pedido |
| `Indice invalido` | Índice negativo informado | Use índice >= 0 |
| `Indice maior que o esperado` | Índice maior que a quantidade de itens | Use um índice válido |
| `Nao existe empresa com esse nome` | Nenhuma empresa com o nome informado para este dono | Verifique o nome da empresa |

---

## Referência Rápida de Comandos

### Sistema
| Comando | Parâmetros | Retorno | Quem pode usar |
|---------|------------|---------|----------------|
| `zerarSistema` | — | void | Qualquer um |
| `encerrarSistema` | — | void | Qualquer um |

### Usuários
| Comando | Parâmetros | Retorno | Quem pode usar |
|---------|------------|---------|----------------|
| `criarUsuario` | nome, email, senha, endereco | void | Qualquer um |
| `criarUsuario` | nome, email, senha, endereco, cpf | void | Qualquer um |
| `login` | email, senha | int (id) | Qualquer um |
| `getAtributoUsuario` | id, atributo | String | Qualquer um |

**Atributos de usuário:** `nome`, `email`, `senha`, `endereco`, `cpf`

### Empresas
| Comando | Parâmetros | Retorno | Quem pode usar |
|---------|------------|---------|----------------|
| `criarEmpresa` | tipoEmpresa, dono, nome, endereco, tipoCozinha | int (id) | Apenas Donos |
| `getEmpresasDoUsuario` | idDono | String (lista) | Qualquer um |
| `getAtributoEmpresa` | empresa, atributo | String | Qualquer um |
| `getIdEmpresa` | idDono, nome, indice | int | Qualquer um |

**Atributos de empresa:** `nome`, `endereco`, `tipoCozinha`, `dono`

### Produtos
| Comando | Parâmetros | Retorno | Quem pode usar |
|---------|------------|---------|----------------|
| `criarProduto` | empresa, nome, valor, categoria | int (id) | Donos |
| `editarProduto` | produto, nome, valor, categoria | void | Donos |
| `getProduto` | nome, empresa, atributo | String | Qualquer um |
| `listarProdutos` | empresa | String (lista) | Qualquer um |

**Atributos de produto:** `valor`, `categoria`, `empresa`

### Pedidos
| Comando | Parâmetros | Retorno | Quem pode usar |
|---------|------------|---------|----------------|
| `criarPedido` | cliente, empresa | int (número) | Apenas Clientes |
| `adicionarProduto` | numero, produto | void | Clientes |
| `getPedidos` | numero, atributo | String | Qualquer um |
| `fecharPedido` | numero | void | Clientes |
| `removerProduto` | pedido, produto | void | Clientes |
| `getNumeroPedido` | cliente, empresa, indice | int | Qualquer um |

**Atributos de pedido:** `cliente`, `empresa`, `estado`, `produtos`, `valor`

**Estados do pedido:** `aberto`, `preparando`
