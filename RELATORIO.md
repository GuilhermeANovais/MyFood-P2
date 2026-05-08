# 📋 Relatório do Projeto — MyFood (Milestone 1 & 2)

**Instituição:** Universidade Federal de Alagoas — IC/UFAL  
**Disciplina:** Programação 2  
**Aluno:** Guilherme A. Novais  
**Repositório:** https://github.com/GuilhermeANovais/MyFood-P2  
**Data de conclusão:** 07/05/2026  

---

## 1. Visão Geral

O **MyFood** é um sistema backend de delivery de alimentos desenvolvido em **Java puro (JDK 11+)**, sem dependências externas. Ele é validado por testes de aceitação via framework **EasyAccept**, que invoca os métodos da `Facade` por reflexão.

O projeto foi implementado em dois milestones:
- **Milestone 1 (US1–US4):** Usuários, Empresas, Produtos e Pedidos
- **Milestone 2 (US5–US8):** Novos tipos de empresa (Mercado e Farmácia), Entregadores e Entregas

---

## 2. Tecnologias e Restrições

| Item | Decisão |
|------|---------|
| Linguagem | Java puro (JDK 11+) |
| Dependências externas | **Nenhuma** (sem Maven, Gradle, libs externas) |
| Persistência | Arquivos `.json` manuais em `data/` |
| Serialização JSON | `JsonUtil.java` — parser/serializer 100% manual |
| Interface | Sem GUI — integração via EasyAccept |
| Banco de dados | Nenhum (zero JDBC, H2, SQLite) |
| Testes | EasyAccept (`.jar` fornecido pelo professor) |

---

## 3. Arquitetura do Sistema

O projeto segue uma **arquitetura em camadas** com os seguintes padrões de projeto:

```
EasyAccept
    │
    ▼
Facade  ─── único ponto de entrada
    │
    ├── UsuarioService   →  UsuarioRepository   →  usuarios.json
    ├── EmpresaService   →  EmpresaRepository   →  empresas.json
    ├── ProdutoService   →  ProdutoRepository   →  produtos.json
    ├── PedidoService    →  PedidoRepository    →  pedidos.json
    └── EntregaService   →  EntregaRepository   →  entregas.json
                         →  EntregadorEmpresaRepository
                              →  entregador_empresa.json
```

### Padrões de Projeto Implementados

| Padrão | Onde é aplicado |
|--------|----------------|
| **Facade** | `facade.Facade` — único ponto de entrada para o EasyAccept |
| **Repository** | Uma classe Repository por entidade, isolando persistência JSON |
| **Singleton (implícito)** | Facade instancia uma vez cada Repository/Service |
| **Factory Method** | `EmpresaService.criarEmpresa()` cria Restaurante, Mercado ou Farmácia conforme `tipoEmpresa` |
| **Herança / Polimorfismo** | `Usuario` → `Cliente`, `DonoEmpresa`, `Entregador`; `Empresa` → `Restaurante`, `Mercado`, `Farmacia` |

---

## 4. Estrutura de Pacotes

```
src/
├── br/ufal/ic/myfood/
│   ├── Main.java              ← ponto de entrada principal
│   ├── Facade.java            ← bridge EasyAccept → sistema
│   ├── models/                ← classes de modelo (legado Milestone 1)
│   └── exceptions/            ← exceptions customizadas
├── facade/
│   └── Facade.java            ← Facade principal (usada pelo EasyAccept)
├── model/
│   ├── Usuario.java           ← abstract
│   ├── Cliente.java
│   ├── DonoEmpresa.java
│   ├── Entregador.java        ← novo no Milestone 2
│   ├── Empresa.java           ← abstract
│   ├── Restaurante.java
│   ├── Mercado.java           ← novo no Milestone 2
│   ├── Farmacia.java          ← novo no Milestone 2
│   ├── Produto.java
│   ├── Pedido.java
│   ├── ItemPedido.java
│   └── Entrega.java           ← novo no Milestone 2
├── service/
│   ├── UsuarioService.java
│   ├── EmpresaService.java
│   ├── ProdutoService.java
│   ├── PedidoService.java
│   └── EntregaService.java    ← novo no Milestone 2
├── repository/
│   ├── UsuarioRepository.java
│   ├── EmpresaRepository.java
│   ├── ProdutoRepository.java
│   ├── PedidoRepository.java
│   ├── EntregaRepository.java             ← novo no Milestone 2
│   └── EntregadorEmpresaRepository.java   ← novo no Milestone 2
└── util/
    └── JsonUtil.java          ← parser/serializer JSON manual
```

---

## 5. User Stories Implementadas

### 🟢 US1 — Criação de Contas (Milestone 1)

**Objetivo:** Permitir o cadastro e autenticação de usuários (Clientes e Donos de Empresa).

**Funcionalidades implementadas:**
- `criarUsuario(nome, email, senha, endereco)` → cria **Cliente** (sem CPF)
- `criarUsuario(nome, email, senha, endereco, cpf)` → cria **Dono de Empresa** (com CPF)
- `login(email, senha)` → retorna ID do usuário autenticado
- `getAtributoUsuario(id, atributo)` → retorna atributos: `nome`, `email`, `senha`, `endereco`, `cpf`

**Validações implementadas:**
- Nome, email, senha e endereço não podem ser vazios
- Email deve conter `@` e ser único no sistema
- CPF deve ter exatamente **14 caracteres** (formato `123.456.789-00`)
- Credenciais inválidas lançam `"Login ou senha invalidos"`

**Mensagens de erro exatas:**
```
"Usuario nao cadastrado."
"Conta com esse email ja existe"
"CPF invalido"
"Nome invalido"
"Email invalido"
"Senha invalido"
"Endereco invalido"
"Login ou senha invalidos"
```

---

### 🟢 US2 — Criação de Empresas (Milestone 1)

**Objetivo:** Gerenciar restaurantes cadastrados por Donos de Empresa.

**Funcionalidades implementadas:**
- `criarEmpresa(tipoEmpresa, dono, nome, endereco, tipoCozinha)` → cria restaurante
- `getEmpresasDoUsuario(idDono)` → lista `{[[Nome, Endereço], ...]}`
- `getAtributoEmpresa(empresa, atributo)` → retorna `nome`, `endereco`, `tipoCozinha`, `dono`
- `getIdEmpresa(idDono, nome, indice)` → ID da empresa pelo índice

**Regras de negócio implementadas:**
- Apenas Donos de Empresa podem criar empresas
- Donos **diferentes** não podem ter empresas com o **mesmo nome**
- O **mesmo dono** pode ter empresas com mesmo nome em **endereços diferentes**
- O **mesmo dono** não pode ter duas empresas com **mesmo nome e mesmo endereço**

**Mensagens de erro exatas:**
```
"Empresa com esse nome ja existe"
"Proibido cadastrar duas empresas com o mesmo nome e local"
"Usuario nao pode criar uma empresa"
"Atributo invalido"
"Empresa nao cadastrada"
"Indice maior que o esperado"
"Nao existe empresa com esse nome"
"Nome invalido"
"Indice invalido"
```

---

### 🟢 US3 — Criação de Produtos (Milestone 1)

**Objetivo:** Gerenciar produtos das empresas.

**Funcionalidades implementadas:**
- `criarProduto(empresa, nome, valor, categoria)` → cria produto
- `editarProduto(produto, nome, valor, categoria)` → edita produto existente
- `getProduto(nome, empresa, atributo)` → retorna `valor` (2 casas decimais), `categoria`, `empresa` (nome)
- `listarProdutos(empresa)` → lista `{[Produto1, Produto2]}`

**Regras de negócio:**
- Nome único por empresa (pode existir em empresas diferentes)
- Valor não pode ser negativo
- Valores monetários sempre com 2 casas decimais (`"5.00"`, `"3.50"`)

---

### 🟢 US4 — Criação de Pedidos (Milestone 1)

**Objetivo:** Fluxo completo de pedidos: criar, adicionar produtos, fechar, remover.

**Funcionalidades implementadas:**
- `criarPedido(cliente, empresa)` → cria pedido no estado `"aberto"`
- `adcionarProduto(numero, produto)` → adiciona produto ao pedido (typo intencional do enunciado)
- `getPedidos(numero, atributo)` → retorna `cliente`, `empresa`, `estado`, `produtos`, `valor`
- `fecharPedido(numero)` → muda estado para `"preparando"`
- `removerProduto(pedido, produto)` → remove **uma** ocorrência do produto
- `getNumeroPedido(cliente, empresa, indice)` → número do pedido pelo índice (mais antigo = 0)

**Regras de negócio:**
- Apenas Clientes podem fazer pedidos (Donos de Empresa não)
- Máximo 1 pedido **em aberto** por cliente por empresa
- O mesmo produto pode ser adicionado múltiplas vezes
- `removerProduto` remove apenas **uma ocorrência**
- Pedidos fechados não podem ser modificados

**Estados do pedido:** `aberto` → `preparando` → `pronto` → `entregando` → `entregue`

---

### 🟢 US5 — Mercados (Milestone 2)

**Objetivo:** Suporte ao tipo de empresa **Mercado** com horário de funcionamento.

**Funcionalidades implementadas:**
- `criarEmpresa("mercado", dono, nome, endereco, abre, fecha, tipoMercado)` → cria mercado
- `alterarFuncionamento(mercado, abre, fecha)` → atualiza horário de abertura/fechamento
- `getAtributoEmpresa(empresa, "abre")` → horário de abertura
- `getAtributoEmpresa(empresa, "fecha")` → horário de fechamento
- `getAtributoEmpresa(empresa, "tipoMercado")` → tipo do mercado

**Validações de horário:**
- Formato obrigatório: `HH:MM` (5 caracteres, `:` na posição 2)
- Horas: 0–23 | Minutos: 0–59
- Horário de fechamento deve ser **após** o de abertura
- Erros: `"Horario invalido"` e `"Formato de hora invalido"`

---

### 🟢 US6 — Farmácias (Milestone 2)

**Objetivo:** Suporte ao tipo de empresa **Farmácia** com regras específicas.

**Funcionalidades implementadas:**
- `criarEmpresa("farmacia", dono, nome, endereco, aberto24Horas, numeroFuncionarios)` → cria farmácia
- `getAtributoEmpresa(empresa, "aberto24horas")` → `true`/`false`
- `getAtributoEmpresa(empresa, "numeroFuncionarios")` → número de funcionários

**Prioridade de entregas:** pedidos de farmácia têm prioridade sobre demais.

---

### 🟢 US7 — Entregadores (Milestone 2)

**Objetivo:** Gerenciar entregadores e suas vinculações com empresas.

**Funcionalidades implementadas:**
- `criarUsuario(nome, email, senha, endereco, veiculo, placa)` → cria **Entregador**
- `cadastrarEntregador(empresa, entregador)` → vincula entregador a uma empresa
- `getEntregadores(empresa)` → lista emails dos entregadores da empresa
- `getEmpresas(entregador)` → lista empresas do entregador no formato `{[[Nome, Endereco]]}`
- `liberarPedido(numero)` → muda estado do pedido para `"pronto"` (disponível para entrega)
- `obterPedido(entregador)` → retorna número do próximo pedido a entregar (farmácias têm prioridade)

---

### 🟢 US8 — Entregas (Milestone 2)

**Objetivo:** Gerenciar o ciclo de vida de uma entrega.

**Funcionalidades implementadas:**
- `criarEntrega(pedido, entregador, destino)` → cria entrega, muda pedido para `"entregando"`
- `getEntrega(id, atributo)` → retorna `cliente`, `empresa`, `pedido`, `entregador`, `destino`, `produtos`
- `getIdEntrega(pedido)` → ID da entrega pelo número do pedido
- `entregar(entrega)` → finaliza entrega, muda pedido para `"entregue"`

**Regras de negócio:**
- Pedido precisa estar no estado `"pronto"` para gerar entrega
- Entregador não pode estar em outra entrega ativa
- Destino usa endereço do cliente se não informado

---

## 6. Persistência de Dados

Todos os dados são armazenados em arquivos JSON na pasta `data/`, manipulados via `java.io` padrão (sem libs externas):

| Arquivo | Conteúdo |
|---------|---------|
| `data/usuarios.json` | Clientes, Donos de Empresa e Entregadores |
| `data/empresas.json` | Restaurantes, Mercados e Farmácias |
| `data/produtos.json` | Produtos cadastrados por empresa |
| `data/pedidos.json` | Pedidos e seus estados |
| `data/entregas.json` | Entregas criadas |
| `data/entregador_empresa.json` | Vínculo entregador ↔ empresa |

### Ciclo de vida

```
Facade()           → todos os Repositories executam load() (carrega do JSON para memória)
zerarSistema()     → limpa memória + reseta nextId=1 + salva arquivos vazios
encerrarSistema()  → salva todos os dados em memória nos arquivos JSON
```

### IDs auto-incrementais

Cada Repository mantém `nextId`, persistido no JSON, garantindo IDs únicos e crescentes entre sessões.

---

## 7. Resultados dos Testes de Aceitação

### Milestone 1 — Resultado Final: ✅ 203/203 Testes Aprovados

| Arquivo | Descrição | Testes | Resultado |
|---------|-----------|--------|-----------|
| `us1_1.txt` | Criação de usuários (sem persistência) | 32 | ✅ PASSOU |
| `us1_2.txt` | Criação de usuários (com persistência) | 11 | ✅ PASSOU |
| `us2_1.txt` | Criação de empresas (sem persistência) | 35 | ✅ PASSOU |
| `us2_2.txt` | Criação de empresas (com persistência) | 13 | ✅ PASSOU |
| `us3_1.txt` | Criação de produtos (sem persistência) | 32 | ✅ PASSOU |
| `us3_2.txt` | Criação de produtos (com persistência) | 11 | ✅ PASSOU |
| `us4_1.txt` | Criação de pedidos (sem persistência) | 52 | ✅ PASSOU |
| `us4_2.txt` | Criação de pedidos (com persistência) | 17 | ✅ PASSOU |

### Milestone 2 (US5–US8)

| Arquivo | Descrição |
|---------|-----------|
| `us5_1.txt` | Mercados (sem persistência) |
| `us5_2.txt` | Mercados (com persistência) |
| `us6_1.txt` | Farmácias (sem persistência) |
| `us6_2.txt` | Farmácias (com persistência) |
| `us7_1.txt` | Entregadores (sem persistência) |
| `us7_2.txt` | Entregadores (com persistência) |
| `us8_1.txt` | Entregas (sem persistência) |
| `us8_2.txt` | Entregas (com persistência) |

---

## 8. Hierarquia de Classes

### Usuários

```
Usuario (abstract)
├── Cliente            → sem CPF, pode fazer pedidos
├── DonoEmpresa        → com CPF, pode criar empresas e produtos
└── Entregador         → com veiculo e placa, realiza entregas
```

### Empresas

```
Empresa (abstract)
├── Restaurante        → tipoCozinha
├── Mercado            → abre, fecha, tipoMercado
└── Farmacia           → aberto24Horas, numeroFuncionarios
```

---

## 9. Decisões Técnicas Importantes

| Decisão | Justificativa |
|---------|--------------|
| **Typo `adcionarProduto`** | Mantido propositalmente — nome exato nos arquivos de teste do EasyAccept |
| **`float` para valores** | Valores exibidos com `String.format(Locale.US, "%.2f", valor)` para garantir 2 casas decimais |
| **JSON manual** | Restrição do enunciado — nenhuma lib externa permitida |
| **Herança para Empresa/Usuario** | Permite `instanceof` nas validações e extensão fácil para Milestone 2 |
| **`Locale.US` no format** | Garante ponto decimal (`.`) ao invés de vírgula em ambientes pt-BR |
| **Prioridade de farmácia em `obterPedido`** | Pedidos de farmácia têm prioridade sobre outros no despacho |

---

## 10. Como Executar

### Pré-requisitos
- JDK 11 ou superior (`java -version`)
- Arquivo `easyaccept.jar` na pasta `lib/`

### Compilar

**Linux/Mac (bash):**
```bash
javac -d out -cp "lib/easyaccept.jar" @sources.txt
```

**Windows (PowerShell):**
```powershell
javac -d out -cp "lib/easyaccept.jar" "@sources.txt"
```

### Rodar um teste individual (Windows)
```bash
java -cp "out;lib/easyaccept.jar" easyaccept.EasyAccept facade.Facade tests/us1_1.txt
```

### Rodar todos os testes
```bash
./run_tests.sh    # Linux/Mac
```

### Rodar todos os testes (Windows)
```powershell
java -cp "out;lib\easyaccept.jar" br.ufal.ic.myfood.Main
```

---

## 11. Arquivos de Documentação do Projeto

| Arquivo | Conteúdo |
|---------|---------|
| `RELATORIO.md` | Este relatório |
| `ARQUITETURA.md` | Documentação técnica da arquitetura e padrões de projeto |
| `WALKTHROUGH.md` | Guia de uso, comandos, erros comuns e referência rápida |
| `PROJECT.md` | Prompt de especificação do Milestone 1 |
| `TEST_RESULTS.md` | Resultados detalhados dos testes de aceitação |
| `CLAUDE.md` | Notas técnicas do agente para desenvolvimento |

---

*Gerado em 07/05/2026 — MyFood-P2 — UFAL IC*
