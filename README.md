# 🏠 SENA - Encontre seu lar com segurança e transparência

O **SENA** é uma plataforma digital inovadora voltada para a busca e locação de moradias temporárias. Inspirado em conceitos de economia colaborativa, o diferencial da plataforma está no foco na **segurança urbana** e na **transparência do entorno**, mapeando informações críticas de iluminação pública, condições das vias e segurança do bairro com base no feedback real da comunidade local.

Este projeto foi desenvolvido como parte de um projeto acadêmico de **Engenharia de Software**, com o backend inteiramente implementado em **Java SE Puro** (sem o uso de frameworks corporativos como Spring Boot) para demonstrar sólidos conceitos de programação orientada a objetos (POO) e controle de concorrência.

---

## 🎯 Diferenciais do Projeto
* **Contexto Urbano Real**: Informações detalhadas sobre a segurança do bairro, qualidade da iluminação e estado de conservação das vias públicas.
* **Avaliação de Moradia**: Módulo colaborativo onde hóspedes podem dar notas de 1 a 5 estrelas em categorias de segurança e comodidade, atualizando as médias do imóvel de forma dinâmica.
* **Exploração de Bairros**: Visualização interativa e gráfica (modal) das notas médias de segurança, infraestrutura e qualidade de vida de cada bairro com a listagem imediata de imóveis disponíveis na região.
* **Painel Administrativo Desktop (Java Swing)**: Interface gráfica para gerenciar a inicialização do servidor HTTP e diagnosticar conexões com o banco de dados.

---

## 📂 Arquitetura do Backend (Padrão MVC)

O código-fonte do backend está estruturado seguindo o padrão de projeto **MVC (Model-View-Controller)** para garantir organização, separação de responsabilidades e facilidade de manutenção:

```
backend/
├── config/
│   └── db.properties          # Configurações de credenciais do banco de dados
├── controller/
│   ├── AbstractHttpHandler.java # Classe base abstrata para manipulação HTTP
│   ├── StaticFileHandler.java   # Servidor de arquivos estáticos (HTML/CSS/JS)
│   ├── LoginHandler.java        # Handler de autenticação de usuários
│   ├── CadastroHandler.java     # Handler de criação de novas contas
│   ├── CadastroImovelHandler.java # Handler de anúncio de moradias
│   ├── ChatHandler.java         # Handler do chatbot do suporte
│   └── AvaliarHandler.java      # Handler de envio de avaliações de moradia
├── database/
│   ├── DatabaseConnection.java  # Conector JDBC baseado no padrão Singleton
│   ├── DAO.java                 # Interface genérica para persistência (CRUD)
│   ├── UsuarioDAO.java          # Implementação de banco para usuários
│   └── ImovelDAO.java           # Implementação de banco para imóveis/avaliações
├── exception/
│   ├── SenaException.java       # Exceção base do domínio
│   ├── DatabaseException.java   # Erros de persistência
│   └── ...                      # Demais exceções de validação de negócios
├── gui/
│   └── ServerConsoleFrame.java  # Janela de controle Swing com controle de Threads
├── model/
│   ├── TipoConta.java           # Enum de tipos de perfil de conta
│   ├── Usuario.java             # Classe abstrata base de usuários
│   ├── Hospede.java             # Subclasse representando hóspedes
│   ├── Anfitriao.java           # Subclasse representando anfitriões
│   ├── Imovel.java              # Modelo de dados de moradias
│   └── Avaliacao.java           # Modelo de dados de avaliações
├── lib/
│   ├── postgresql.jar           # Driver JDBC do PostgreSQL para Supabase
│   └── ...                      # Dependências extras para utilitários
└── SENAWeb.java                 # Classe de entrada principal (Main)
```

---

## 🛠️ Tecnologias Utilizadas

### Backend
* **Linguagem**: Java SE 25 (Core)
* **Banco de Dados**: PostgreSQL em nuvem (hospedado no **Supabase**)
* **Persistência**: **JDBC** (driver nativo do PostgreSQL)
* **Servidor Web**: `com.sun.net.httpserver` nativo
* **GUI Administrativa**: Java Swing

### Frontend
* HTML5 semântico
* CSS3 Vanilla (com variáveis de estilo e layouts responsivos)
* JavaScript moderno (Vanilla JS com chamadas AJAX `fetch`)

---

## ☕ Conceitos de POO & Java Aplicados

Este projeto serve como demonstração prática dos principais fundamentos da orientação a objetos e da biblioteca padrão do Java:

1. **Abstração**: Classes abstratas ([Usuario.java](file:///c:/Users/luisf/Sena/backend/model/Usuario.java)) e interfaces genéricas ([DAO.java](file:///c:/Users/luisf/Sena/backend/database/DAO.java)) delimitando contratos claros de implementação.
2. **Herança**: Especialização de usuários (`Hospede`, `Anfitriao` herdando de `Usuario`) e reuso de lógica comum em handlers HTTP.
3. **Polimorfismo**: Instanciação dinâmica com base no tipo de conta e substituição de fluxos de logs usando classes anônimas de `OutputStream`.
4. **Sobrescrita & Sobrecarga**: Redefinição de métodos como `toString()`, além de construtores de exceções sobrecarregados para capturar a causa raiz de erros.
5. **Encapsulamento**: Atributos definidos como `private` e expostos com validações via getters/setters públicos.
6. **Thread (Concorrência)**: Inicialização assíncrona do servidor web e de diagnósticos de banco de dados para evitar travamentos da interface Swing.
7. **Tratamento de Exceções**: Criação de hierarquia própria de erros capturados via blocos `try-catch` para envio de códigos HTTP correspondentes.
8. **Java IO**: Leitura e parsing de arquivos de propriedades, logs e arquivos estáticos mapeando Content-Types corretamente.

---

## 🚀 Como Instalar e Executar

### 1. Configuração do Supabase (Banco de Dados)
1. Crie uma conta no [Supabase](https://supabase.com/) e inicie um novo projeto.
2. No painel do seu projeto, acesse a guia **SQL Editor** (ícone com símbolo `>_`).
3. Clique em **"+ New query"**, copie o conteúdo completo do arquivo [schema.sql](file:///c:/Users/luisf/Sena/schema.sql) deste repositório, cole e clique em **"Run"**. Isso criará as tabelas e adicionará a base inicial de imóveis e avaliações.

### 2. Configuração das Credenciais do Banco
1. No menu principal do seu projeto no Supabase, clique no botão **Connect** (ou na seção *Direct Connection string*).
2. Na aba **URI**, identifique o seu endereço de host (ex: `db.xxxxxx.supabase.co`).
3. Abra o arquivo [backend/config/db.properties](file:///c:/Users/luisf/Sena/backend/config/db.properties) e atualize os campos com as suas credenciais:
   ```properties
   db.url=jdbc:postgresql://SEU_HOST_DO_SUPABASE:5432/postgres
   db.user=postgres
   db.password=SUA_SENHA_DO_BANCO_DE_DADOS
   ```

### 3. Execução
No Windows, criamos um script facilitador que compila todas as classes e executa a aplicação com os classpaths corretos em apenas um comando. 

Abra o terminal do projeto (ou terminal do VS Code) e execute:
```powershell
.\run.bat
```

Após a compilação, o **Painel do Servidor (Java Swing)** abrirá na sua tela.
1. Clique em **Testar Conexão Supabase** para checar a integração.
2. Clique em **Iniciar Servidor** para deixar o sistema online.
3. Abra o navegador e acesse: [http://localhost:8080/login.html](http://localhost:8080/login.html).

---

## 👥 Equipe de Desenvolvimento

Conheça os estudantes de Engenharia de Software responsáveis pela criação da plataforma:

* **Luís Felipe de Sousa Fialho** - *Desenvolvedor Full-Stack*
  * Responsável pelo desenvolvimento de toda a arquitetura de backend do sistema em Java (MVC, JDBC Supabase, Threads e interface Swing) e lógica de controle e integração dinâmica do frontend.
  
* **Isaque Caios Mota Leal** - *UX/UI Designer*
  * Responsável pela concepção visual, experiência do usuário e design das interfaces do portal, garantindo um layout moderno, limpo, intuitivo e responsivo.

* **Guilherme Pinho Lima Alves** - *Analista de Dados*
  * Responsável pela modelagem relacional de banco de dados, definição do schema físico de dados e integridade no Supabase.

* **Gabriel Silva** - *Product Manager*
  * Responsável pela definição dos requisitos de negócios, levantamento de regras de produto e coordenação ágil das metas e entregas.
