# MyFood — Documentação de Arquitetura

## Visão Geral do Sistema

### O que é o MyFood
MyFood é um sistema de delivery de alimentos desenvolvido como projeto universitário (UFAL IC). O sistema permite que clientes façam pedidos em restaurantes e que donos de empresa gerenciem seus estabelecimentos e produtos.

### Escopo do Milestone 1 (User Stories 1-4)
O Milestone 1 implementa as funcionalidades core do sistema:
- **US1**: Criação e autenticação de usuários (Clientes e Donos de Empresa)
- **US2**: Criação e gerenciamento de restaurantes
- **US3**: Criação e gerenciamento de produtos
- **US4**: Criação e gerenciamento de pedidos

### Arquitetura em Camadas

```
┌─────────────────────────────────────────────────────────────────┐
│                        EasyAccept                                │
│                    (Testes de Aceitação)                         │
└─────────────────────────┬───────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│                         Facade                                  │
│              (Único ponto de entrada do sistema)                │
│         facade.Facade — delega para services                     │
└─────────────────────────┬───────────────────────────────────────┘
                          │
          ┌───────────────┼───────────────┬───────────────┐
          ▼               ▼               ▼               ▼
┌─────────────────┐ ┌───────────┐ ┌───────────┐ ┌───────────────┐
│ UsuarioService  │ │EmpresaServ│ │ProdutoServ│ │  PedidoService │
└────────┬────────┘ └─────┬─────┘ └─────┬─────┘ └──────┬────────┘
         │                │             │              │
         ▼                ▼             ▼              ▼
┌─────────────────┐ ┌───────────┐ ┌───────────┐ ┌───────────────┐
│UsuarioRepository│ │EmpresaRepo │ │ProdutoRepo│ │ PedidoRepository│
└────────┬────────┘ └─────┬─────┘ └─────┬─────┘ └──────┬────────┘
         │                │             │              │
         └────────────────┴─────────────┴──────────────┘
                              │
                              ▼
                    ┌─────────────────┐
                    │     JsonUtil    │
                    │  (Serialização) │
                    └────────┬────────┘
                             │
                             ▼
                    ┌─────────────────┐
                    │   data/*.json   │
                    │  (Persistência) │
                    └─────────────────┘
```

---

## Estrutura de Pacotes

### `facade/`
**Responsabilidade:** Único ponto de entrada para todos os comandos do sistema. Orquestra os services e expõe a interface pública.

| Classe | Descrição |
|--------|-----------|
| `Facade` | Centraliza todas as operações do sistema, delega para services correspondentes |

### `model/`
**Responsabilidade:** Entidades de domínio do sistema (POJOs).

| Classe | Descrição |
|--------|-----------|
| `Usuario` | Classe abstrata base com atributos comuns (id, nome, email, senha, endereco) |
| `Cliente` | Extende Usuario — usuário que pode fazer pedidos |
| `DonoEmpresa` | Extende Usuario — usuário que pode criar empresas e produtos (adiciona CPF) |
| `Empresa` | Classe abstrata base para establishments (id, nome, endereco, tipoCozinha, idDono) |
| `Restaurante` | Implementa Empresa — tipo de empresa disponível no Milestone 1 |
| `Produto` | Produto vendido por uma empresa (id, nome, valor, categoria, idEmpresa) |
| `Pedido` | Pedido de um cliente a uma empresa (numero, idCliente, idEmpresa, estado, produtos) |
| `ItemPedido` | Item individual em um pedido (idProduto, quantidade) — presente mas não diretamente usado na estrutura atual |

### `service/`
**Responsabilidade:** Regras de negócio e lógica de aplicação.

| Classe | Descrição |
|--------|-----------|
| `UsuarioService` | Validação e criação de usuários, autenticação (login) |
| `EmpresaService` | Validação e criação de empresas, regras de nome duplicado |
| `ProdutoService` | CRUD de produtos, validações |
| `PedidoService` | Fluxo de pedidos: criar, adicionar produtos, fechar, remover |

### `repository/`
**Responsabilidade:** Persistência e acesso aos dados (padrão Repository).

| Classe | Descrição |
|--------|-----------|
| `UsuarioRepository` | Persiste usuários em `usuarios.json` |
| `EmpresaRepository` | Persiste empresas em `empresas.json` |
| `ProdutoRepository` | Persiste produtos em `produtos.json` |
| `PedidoRepository` | Persiste pedidos em `pedidos.json` |

### `util/`
**Responsabilidade:** Utilitários de serialização JSON.

| Classe | Descrição |
|--------|-----------|
| `JsonUtil` | Parser JSON manual — lê/escreve arquivos JSON sem bibliotecas externas |

---

## Modelo de Dados

### Usuario (Abstract)
```java
protected int id;
protected String nome;
protected String email;
protected String senha;
protected String endereco;

public abstract String getTipo(); // "cliente" ou "dono"
```

**Serialização JSON:**
```json
// Cliente
{"id":1,"tipo":"cliente","nome":"Carlos","email":"carlos@email.com","senha":"123","endereco":"Rua X"}

// DonoEmpresa
{"id":2,"tipo":"dono","nome":"João","email":"joao@email.com","senha":"123","endereco":"Rua Y","cpf":"123.456.789-00"}
```

### Cliente extends Usuario
- Sem atributos adicionais
- `getTipo()` retorna `"cliente"`

### DonoEmpresa extends Usuario
- `cpf` (String, 14 caracteres)
- `getTipo()` retorna `"dono"`

### Empresa (Abstract)
```java
protected int id;
protected String nome;
protected String endereco;
protected String tipoCozinha;
protected int idDono;  // FK para Usuario

public abstract String getTipo();
```

### Restaurante extends Empresa
- `getTipo()` retorna `"restaurante"`

**Serialização JSON:**
```json
{"id":1,"tipo":"restaurante","nome":"Pastelaria","endereco":"Rua Segura N 987","tipoCozinha":"brasileira","idDono":2}
```

### Produto
```java
private int id;
private String nome;
private float valor;
private String categoria;
private int idEmpresa;  // FK para Empresa
```

**Serialização JSON:**
```json
{"id":1,"nome":"Pastel de Queijo","valor":5.00,"categoria":"salgado","idEmpresa":1}
```

### Pedido
```java
private int numero;
private int idCliente;    // FK para Usuario
private int idEmpresa;    // FK para Empresa
private String estado;    // "aberto" ou "preparando"
private List<Integer> produtos;  // IDs dos produtos
```

**Serialização JSON:**
```json
{"numero":1,"idCliente":1,"idEmpresa":1,"estado":"aberto","produtos":[1,2,1]}
```

### ItemPedido
```java
private int idProduto;
private int quantidade;
```
*Nota: Esta classe existe no modelo mas a implementação atual de Pedido usa `List<Integer>` diretamente para armazenar IDs de produtos, permitindo múltiplas ocorrências do mesmo produto.*

---

## Padrões de Projeto Implementados

### 1. Facade

**Descrição Geral:** Padrão que fornece uma interface unificada para um conjunto de subsistemas, simplificando a interação com sistemas complexos.

**Problema Resolvido no Projeto:** EasyAccept precisa de um único ponto de entrada para testar todas as funcionalidades. Sem Facade, os testes teriam que conhecer e orquestrar múltiplos services e repositories.

**Onde está implementado:**
- `facade.Facade` — toda a interface pública do sistema

**Trecho de código ilustrativo:**
```java
// Facade.java — único ponto de entrada para pedidos
public int criarPedido(int cliente, int empresa) throws Exception {
    return pedidoService.criarPedido(cliente, empresa, usuarioService, empresaService);
}

public void adicionarProduto(int numero, int produto) throws Exception {
    pedidoService.adicionarProduto(numero, produto, produtoService, empresaService);
}

public void fecharPedido(int numero) throws Exception {
    pedidoService.fecharPedido(numero);
}
```

### 2. Repository

**Descrição Geral:** Padrão que abstrai o acesso à persistência de dados, separando lógica de negócio da lógica de armazenamento.

**Problema Resolvido no Projeto:** Cada entidade (Usuario, Empresa, Produto, Pedido) precisa ser persistida em JSON. O Repository isola essa lógica de serialização/deserialização dos services.

**Onde está implementado:**
- `repository.UsuarioRepository`
- `repository.EmpresaRepository`
- `repository.ProdutoRepository`
- `repository.PedidoRepository`

**Trecho de código ilustrativo:**
```java
// PedidoRepository.java — isolando persistência de Pedido
public class PedidoRepository {
    private List<Pedido> pedidos;
    private int nextId;
    private String filePath;

    public void load() {
        // Lê do JSON e reconstrói objetos
        String content = JsonUtil.readFile(filePath);
        List<String> objs = JsonUtil.parseObjectArray(content, "pedidos");
        for (String obj : objs) {
            int numero = JsonUtil.parseInt(obj, "numero");
            // ... reconstruí pedido
        }
    }

    public void save() {
        // Serializa objetos para JSON
        StringBuilder sb = new StringBuilder();
        sb.append("{\"nextId\":").append(nextId).append(",\"pedidos\":[");
        for (Pedido p : pedidos) {
            sb.append(p.toJson());
        }
        sb.append("]}");
        JsonUtil.writeFile(filePath, sb.toString());
    }
}
```

### 3. Singleton (Implícito via Facade)

**Descrição Geral:** Garante que uma classe tenha apenas uma instância e fornece um ponto global de acesso a ela.

**Problema Resolvido no Projeto:** EasyAccept cria uma única instância de Facade para todos os testes. Cada Facade cria uma instância de cada Repository e Service.

**Onde está implementado:**
- `facade.Facade` — instanciada uma vez por sessão de teste
- Cada Repository/Service é criado uma única vez dentro do Facade

**Trecho de código ilustrativo:**
```java
// Facade.java — única instância
public class Facade {
    private UsuarioRepository usuarioRepository;
    private EmpresaRepository empresaRepository;
    // ...

    public Facade() {
        initRepositories();  // Cria cada repositório uma única vez
        initServices();
    }

    private void initRepositories() {
        usuarioRepository = new UsuarioRepository(DATA_PATH);
        empresaRepository = new EmpresaRepository(DATA_PATH);
        // ...
    }
}
```

### 4. Factory Method

**Descrição Geral:** Padrão que define uma interface para criar objetos mas delega a subclasses a decisão de quais classes concretas instanciar.

**Problema Resolvido no Projeto:** O sistema deve suportar múltiplos tipos de empresa (Restaurante, Mercado, Farmácia) no futuro. O Factory Method permite estenderfacilmente sem modificar o código existente.

**Onde está implementado:**
- `service.EmpresaService.criarEmpresa()` — cria instâncias de `Restaurante`
- Preparado para `Mercado` e `Farmácia` no Milestone 2

**Trecho de código ilustrativo:**
```java
// EmpresaService.java — criação flexível de empresas
public int criarEmpresa(String tipoEmpresa, int dono, String nome,
                         String endereco, String tipoCozinha, UsuarioService usuarioService) throws Exception {
    // ...

    // Currently only Restaurante is supported
    Empresa empresa = new Restaurante(id, nome, endereco, tipoCozinha, dono);

    // Future expansion (Milestone 2):
    // switch (tipoEmpresa.toLowerCase()) {
    //     case "restaurante": return new Restaurante(...);
    //     case "mercado": return new Mercado(...);
    //     case "farmacia": return new Farmacia(...);
    // }

    repository.adicionar(empresa);
    return id;
}
```

---

## Camada de Persistência

### Como o JsonUtil funciona internamente

O `JsonUtil` é um parser JSON manual que não usa bibliotecas externas:

```java
// Leitura: lê arquivo inteiro como String
public static String readFile(String path) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(path));
    StringBuilder sb = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
        sb.append(line);
    }
    reader.close();
    return sb.toString();
}

// Parsing: busca chave e extrai valor por posição
public static String parseString(String json, String key) {
    String searchKey = "\"" + key + "\":";
    int start = json.indexOf(searchKey);
    if (start == -1) return null;
    start += searchKey.length();
    // ...
    if (json.charAt(start) == '"') {
        start++;
        int end = json.indexOf('"', start);
        return json.substring(start, end);
    }
    // ...
}

// Parsing de arrays de objetos (para reconstituir lista)
public static List<String> parseObjectArray(String json, String key) {
    // Busca chave, encontra '[', conta chaves '{' e '}'
    // Retorna lista de JSON strings individuais
}
```

### Estrutura dos arquivos JSON

**`data/usuarios.json`**
```json
{
  "nextId": 3,
  "usuarios": [
    {"id":1,"tipo":"cliente","nome":"Carlos","email":"carlos@email.com","senha":"123","endereco":"Rua X"},
    {"id":2,"tipo":"dono","nome":"João","email":"joao@email.com","senha":"123","endereco":"Rua Y","cpf":"123.456.789-00"}
  ]
}
```

**`data/empresas.json`**
```json
{
  "nextId": 2,
  "empresas": [
    {"id":1,"tipo":"restaurante","nome":"Pastelaria","endereco":"Rua Segura N 987","tipoCozinha":"brasileira","idDono":2}
  ]
}
```

**`data/produtos.json`**
```json
{
  "nextId": 5,
  "produtos": [
    {"id":1,"nome":"Pastel de Queijo","valor":5.00,"categoria":"salgado","idEmpresa":1},
    {"id":2,"nome":"Refrigerante","valor":3.50,"categoria":"bebida","idEmpresa":1}
  ]
}
```

**`data/pedidos.json`**
```json
{
  "nextId": 4,
  "pedidos": [
    {"numero":1,"idCliente":1,"idEmpresa":1,"estado":"preparando","produtos":[1,2,1]}
  ]
}
```

### Ciclo de vida dos dados

1. **Inicialização:** Quando `Facade` é criado, cada `Repository` executa `load()`
2. **Em memória:** Todas as operaçõesworking com dados em memória (Listas)
3. **`zerarSistema`:** Limpa todas as listas e reseta `nextId` para 1, salva arquivo vazio
4. **`encerrarSistema`:** Chama `save()` em todos os repositories, persistindo dados em JSON

```java
public void zerarSistema() {
    usuarioService.clear();  // Limpa lista, reseta nextId, salva
    empresaService.clear();
    produtoService.clear();
    pedidoService.clear();
}

public void encerrarSistema() {
    usuarioService.save();   // Salva estado atual
    empresaService.save();
    produtoService.save();
    pedidoService.save();
}
```

### Estratégia de IDs auto-incrementais

Cada repositório mantém um campo `nextId`:
- Inicia com 1
- Incrementa atomicamente a cada `getNextId()`
- Persistido no JSON para manter consistência entre sessões

```java
public class UsuarioRepository {
    private int nextId;  // Ex: 3 significa próximo usuário terá ID 3

    public int getNextId() {
        return nextId++;  // Retorna valor atual, depois incrementa
    }
}
```

---

## Fluxo de uma Requisição

### Exemplo 1 — criarPedido

```
EasyAccept → Facade.criarPedido(cliente, empresa)
    │
    ▼
Facade.delega para PedidoService.criarPedido()
    │
    ▼
┌──────────────────────────────────────────────────────┐
│ PedidoService.criarPedido():                         │
│  1. Valida: cliente existe? (isDonoEmpresa check)   │
│  2. Valida: não é dono de empresa                    │
│  3. Valida: não existe pedido aberto para esta       │
│     empresa (buscarPedidoAbertoPorClienteEEmpresa)    │
│  4. Cria novo Pedido(numero=getNextId(), cliente,   │
│     empresa, estado="aberto", lista vazia)          │
│  5. Adiciona ao repository                           │
│  6. Retorna número do pedido                         │
└──────────────────────────────────────────────────────┘
    │
    ▼
Retorna int (número do pedido)
```

### Exemplo 2 — fecharPedido

```
EasyAccept → Facade.fecharPedido(numero)
    │
    ▼
Facade.delega para PedidoService.fecharPedido()
    │
    ▼
┌──────────────────────────────────────────────────────┐
│ PedidoService.fecharPedido():                       │
│  1. Busca pedido por número (repository)            │
│  2. Valida: pedido existe?                          │
│  3. Chama pedido.fechar() → estado = "preparando"   │
│  4. Repository já está em memória (nada a fazer)    │
└──────────────────────────────────────────────────────┘
    │
    ▼
Retorna void (EasyAccept não captura retorno)
```

---

## Decisões Técnicas e Trade-offs

### Por que JSON manual em vez de bibliotecas externas

**Restrição do projeto universitário:** O enunciado especificava que não podiam usar bibliotecas externas de JSON.

**Consequências:**
- Parser limitado: não suporta JSON aninhado complexo
- Cada tipo de objeto precisa de lógica manual de parsing
- `nextId` precisa ser explícito porque não há suporte automático

### Por que não usar banco de dados relacional

**Simplicidade:** JSON é mais simples para um projeto universitário — não requer setup de servidor, migrations, ou SQL.

**Adequação ao escopo:** O volume de dados é extremamente baixo (dezenas de registros), então performance não é preocupação.

### Herança de Usuario (Cliente/DonoEmpresa)

**Decisão:** Usar herança de classes para modelar os dois tipos de usuário.

```java
public abstract class Usuario { /* ... */ }

public class Cliente extends Usuario { /* Sem atributos adicionais */ }

public class DonoEmpresa extends Usuario {
    private String cpf;  // Único campo adicional
}
```

**Alternativa considerada:** Usar um campo `tipo` com condições. **Decisão:** Herança é mais limpa e permite `instanceof` para validações específicas.

### Herança de Empresa preparada para Milestone 2

```java
public abstract class Empresa { /* ... */ }

public class Restaurante extends Empresa { /* ... */ }

// Milestone 2:
// public class Mercado extends Empresa { /* ... */ }
// public class Farmacia extends Empresa { /* ... */ }
```

**Decisão:** Classe abstrata com subclasses permite adicionar tipos sem modificar código existente.

### Tratamento de ponto flutuante para valores monetários

**Decisão:** Usar `float` com `String.format(Locale.US, "%.2f", valor)` para exibir.

```java
return String.format(Locale.US, "%.2f", prod.getValor());
```

**Limitação:** `float` pode ter imprecisões (ex: 6.199999...). Para produção, `BigDecimal` seria mais apropriado.

---

## Limitações Conhecidas e Melhorias Futuras

### O que o Milestone 2 adiciona

| Funcionalidade | Descrição |
|----------------|-----------|
| **Tipos de empresa** | Mercado, Farmácia (além de Restaurante) |
| **Entregadores** | Novo tipo de usuário para entregar pedidos |
| **Sistema de entregas** | Rastreamento de pedidos em entrega |
| **Pagamento** | Integração com gateway de pagamento |

### Limitações do parser JSON manual

- Não suporta escape de caracteres especiais além de `"` e `\`
- Não suporta valores `null` ou `true`/`false`
- Arrays aninhados não são suportados
- Sem validação de estrutura JSON

### O que seria diferente em produção

| Aspecto | Desenvolvimento (Atual) | Produção (Recomendado) |
|---------|------------------------|------------------------|
| Persistência | JSON manual em arquivos | Banco de dados relacional (PostgreSQL/MySQL) |
| Valores monetários | `float` | `BigDecimal` |
| Autenticação | Senha em texto (simplificado) | Hash + Salt + JWT |
| Validação | Exceções com strings | Bean Validation (JSR-380) |
| Concorrência |threading básico negligenciado | Conexões gerenciadas, transações |
| API | EasyAccept | REST API com JSON padronizado |
