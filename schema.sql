-- ========================================================
-- SCHEMA DE CRIAÇÃO DAS TABELAS - PLATAFORMA SENA
-- Carregue este script no painel SQL Editor do seu Supabase
-- ========================================================

-- Limpeza de tabelas existentes (opcional)
DROP TABLE IF EXISTS avaliacoes CASCADE;
DROP TABLE IF EXISTS imoveis CASCADE;
DROP TABLE IF EXISTS usuarios CASCADE;

-- 1. TABELA DE USUÁRIOS
CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    senha VARCHAR(100) NOT NULL,
    tipo VARCHAR(50) NOT NULL
);

-- 2. TABELA DE IMÓVEIS
CREATE TABLE imoveis (
    id SERIAL PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    localizacao VARCHAR(100) NOT NULL,
    preco VARCHAR(50) NOT NULL,
    imagem TEXT NOT NULL,
    detalhes VARCHAR(200),
    rating VARCHAR(10),
    badges TEXT,
    estrelas VARCHAR(10),
    endereco VARCHAR(200),
    descricao TEXT,
    comodidades TEXT,
    scores VARCHAR(50),
    proprietario VARCHAR(100)
);

-- 3. TABELA DE AVALIAÇÕES DE MORADIA
CREATE TABLE avaliacoes (
    id SERIAL PRIMARY KEY,
    imovel_id INT REFERENCES imoveis(id) ON DELETE CASCADE,
    avaliacao_geral INT NOT NULL,
    seguranca_bairro INT NOT NULL,
    seguranca_rua INT NOT NULL,
    comodidade INT NOT NULL,
    localizacao INT NOT NULL,
    tags TEXT,
    comentario TEXT,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================================================
-- CARGA DE DADOS INICIAIS (MOCK DATA)
-- ========================================================

-- Inserir Usuários Iniciais
INSERT INTO usuarios (nome, email, senha, tipo) VALUES
('Admin', 'admin@sena.com', '123456', 'ADMINISTRADOR'),
('Luís Felipe', 'luis@email.com', 'Sena123', 'HOSPEDE'),
('Guilherme Pinho', 'guilherme@email.com', 'Sena321', 'ANFITRIAO');

-- Inserir Imóveis Iniciais (7 propriedades, incluindo as 6 da Home e a do PDF)
INSERT INTO imoveis (titulo, localizacao, preco, imagem, detalhes, rating, badges, estrelas, endereco, descricao, comodidades, scores, proprietario) VALUES
(
    'Apartamento Moderno - Ipanema', 
    'Rio de Janeiro', 
    '2.500', 
    'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=800&q=80', 
    '2 quartos • 1 banheiro • WiFi • Ar condicionado', 
    '9.2', 
    'Boa iluminação,Rua boa', 
    '5.0',
    'Av. Vieira Souto, 456 - Ipanema, Rio de Janeiro - RJ',
    'Excelente apartamento moderno a poucos passos da praia de Ipanema. Localizado em andar alto com boa ventilação e claridade natural. Portaria 24 horas, wifi rápido, ar condicionado Split em todos os cômodos e cozinha completa equipada.',
    'Wi-fi,Ar condicionado,Mobiliado,Pet Friendly,Câmeras',
    '9.2,9.0,9.2,9.1',
    'Carlos Silva'
),
(
    'Studio Aconchegante - Vila Madalena', 
    'São Paulo', 
    '1.800', 
    'https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800&q=80', 
    '1 quarto • 1 banheiro • Cozinha • Pet-friendly', 
    '8.7', 
    'Excelente,Ótima', 
    '4.5',
    'Rua Aspicuelta, 789 - Vila Madalena, São Paulo - SP',
    'Studio super charmoso e confortável na melhor localização da Vila Madalena. Condomínio residencial tranquilo próximo ao metrô, padarias, ciclovias e restaurantes locais de alta qualidade. Ideal para solteiros ou casais.',
    'Wi-fi,Mobiliado,Pet Friendly,Estacionamento',
    '8.7,9.2,8.5,8.8',
    'Mariana Souza'
),
(
    'Casa Familiar - Leblon', 
    'Rio de Janeiro', 
    '4.200', 
    'https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=800&q=80', 
    '3 quartos • 2 banheiros • Jardim • Garagem', 
    '9.5', 
    'Excelente,Perfeita', 
    '5.0',
    'Rua General Urquiza, 12 - Leblon, Rio de Janeiro - RJ',
    'Casa ampla e arejada com jardim privativo no coração do Leblon. Ambiente extremamente silencioso e seguro, ideal para famílias. Conta com sala de estar e jantar integradas, 3 quartos espaçosos e garagem privativa fechada.',
    'Estacionamento,Mobiliado,Câmeras,Academia,Pet Friendly',
    '9.5,9.6,9.4,9.5',
    'Ricardo Santos'
),
(
    'Cobertura Luxuosa - Copacabana', 
    'Rio de Janeiro', 
    '5.500', 
    'https://images.unsplash.com/photo-1512917774080-9991f1c4c750?w=800&q=80', 
    '2 quartos • 2 banheiros • Vista mar • Piscina', 
    '8.9', 
    'Muito boa,Regular', 
    '4.5',
    'Avenida Atlântica, 1024 - Copacabana, Rio de Janeiro - RJ',
    'Cobertura duplex espetacular na praia de Copacabana com piscina privativa e vista panorâmica para o mar. Prédio tradicional com segurança 24h e monitoramento por câmeras de alta definição. Mobiliado com sofisticação.',
    'Wi-fi,Piscina,Estacionamento,Mobiliado,Câmeras',
    '8.9,8.5,8.7,9.0',
    'Beatriz Albuquerque'
),
(
    'Apartamento Compacto - Centro', 
    'São Paulo', 
    '1.200', 
    'https://images.unsplash.com/photo-1543482335-97931367092c?w=800&q=80', 
    '1 quarto • 1 banheiro • Próximo ao metrô', 
    '7.8', 
    'Moderada,Precisa melhorar', 
    '3.5',
    'Rua Conselheiro Furtado, 150 - Centro, São Paulo - SP',
    'Apartamento funcional e de fácil acesso no centro de São Paulo. A 200 metros da estação do metrô. Região movimentada com ótimo comércio ao redor, restaurantes tradicionais e conveniências diversas. Condomínio com lavanderia compartilhada.',
    'Wi-fi,Mobiliado,Portaria',
    '7.8,7.5,7.9,8.0',
    'Luiz Fernando'
),
(
    'Frente ao Mar - Barra da Tijuca', 
    'Rio de Janeiro', 
    '3.800', 
    'https://images.unsplash.com/photo-1600585154526-990dced4db0d?w=800&q=80', 
    '2 quartos • 1 banheiro • Vista oceano • Varanda', 
    '9.1', 
    'Excelente,Muito boa', 
    '5.0',
    'Av. Lúcio Costa, 3500 - Barra da Tijuca, Rio de Janeiro - RJ',
    'Desfrute da melhor vista para o oceano nesta varanda magnífica na Barra da Tijuca. Condomínio seguro com infraestrutura de lazer de alto padrão (piscina, sauna, portaria e segurança privada). Apartamento arejado e moderno.',
    'Wi-fi,Estacionamento,Mobiliado,Piscina,Câmeras',
    '9.1,9.3,9.0,9.2',
    'Gabriela Lemos'
),
(
    'Apartamento Solar das Palmeiras', 
    'Pinheiros', 
    '3.500', 
    'https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800&q=80', 
    '2 Quartos • 1 Vaga • 65m²', 
    '8.7', 
    'Seguro,Iluminado,Boa Via,Movimentado', 
    '4.5',
    'Rua das Flores, 234 - Centro, São Paulo - SP',
    'Apartamento moderno e totalmente mobiliado localizado no coração do centro de Pinheiros. Com 85m², oferece 2 quartos, 1 suíte, sala ampla com varanda, cozinha equipada e 1 vaga de garagem coberta. Prédio com portaria 24h, academia e salão de festas.',
    'Wi-fi,Estacionamento,Mobiliado,Pet Friendly,Câmeras,Academia,2 Quartos + 1 Suíte,2 Banheiros,85 m²,1 Vaga de Garagem',
    '7.5,9.2,8.8,8.7',
    'João Santos'
);

-- Inserir Algumas Avaliações de Exemplo para o Imóvel 'Apartamento Solar das Palmeiras' (ID 7)
INSERT INTO avaliacoes (imovel_id, avaliacao_geral, seguranca_bairro, seguranca_rua, comodidade, localizacao, tags, comentario) VALUES
(7, 5, 5, 5, 5, 5, 'Rua bem iluminada,Região segura', 'Rua bem iluminada e segura durante a noite. Me sinto muito tranquila ao voltar para casa tarde.'),
(7, 4, 4, 4, 4, 4, 'Comércio próximo,Transporte acessível', 'Boa localização e comércio próximo. Supermercado, farmácia e padaria a poucos metros.'),
(7, 5, 5, 5, 5, 5, 'Rua bem iluminada,Região segura,Pouco movimento', 'Apartamento excelente e bairro muito seguro. Recomendo!');
