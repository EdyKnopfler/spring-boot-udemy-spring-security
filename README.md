# Apanhando do Spring Security

Aqui aceitamos autenticação via:
* form de login
* HTTP Basic
* JWT token (programado "manuaticamente")

E é incrível como os 3 tipos conseguem coexistir!

## Para rodar

Na primeira inicialização o database H2 estará vazio e será preciso um usuário administrador cadastrado para que outros usuários possam ser inseridos.

O console do H2 é protegido e o acesso é dado somente aos usuários de perfil ADMIN. Para a criação do primeiro administrador, 
esta regra pode ser relaxada temporariamente mudando o código em `SegurancaConfig.java`, bastando mudar o _antMatcher_ do array de admins para
o array de permitidos.

A opção pelo H2 como database foi feita para simplificação; outros servidores de bancos de dados não apresentarão esse tipo de questão.

## Endpoints

* `GET /` (home): conteúdo aberto
* `GET /login` e `GET /logout`: fornecidos pelo Spring usando as configurações padrão
* `GET /confidencial`: aceita acesso somente logado
  *  Via sessão (use o form de login)
  *  Cabeçalho [HTTP Basic](https://developer.mozilla.org/pt-BR/docs/Web/HTTP/Authentication): `Authorization: Basic ...`
  *  Cabeçalho JWT: `Authorization: Bearer...`
* `POST /usuarios/autenticar`: autentica um usuário e devolve o token JWT
* `POST /usuarios`: cadastro de usuários (somente usuários ADMIN)
* `GET /h2-console`: console do database H2 (somente usuários ADMIN)

## Pontos importantes (cheios de anotações)

### SegurancaConfig.java

* Usa o `SecurityFilterChain` para configurar as rotas abertas, fechadas e de papéis (roles) específicos
* Configura o filtro que autentica a requisição na presença do token JWT
  * `JwtAuthFilter.java`
  * Logo antes do padrão do Spring (`UsernamePasswordAuthenticationFilter`)

### JwtAuthFilter.java

Na presença de um token JWT válido (parseável e dentro do período de expiração), insere o usuário correspondente
no `SecurityContextHolder` do Spring.

### application-development.properties

Contém uma amostra de chave secreta para uso em tempo de desenvolvimento, configure o projeto para rodar com
`-Dspring.profiles.active=development`

### UsuarioServiceImpl.java

* Fornece um `UserDetailsService` para o Spring
  * usado automaticamente pelos logins via form e HTTP Basic (chique no úrtimo)
  * também requisitado na verificação de token JWT
* Utiliza um repositório de usuários

