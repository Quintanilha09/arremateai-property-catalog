package com.arremateai.propertycatalog.config;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.util.PropertyPlaceholderHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Externalização de Configurações do Property-Catalog (E1-H4)")
class ExternalizacaoConfiguracoesTest {

    private static String conteudoApplicationProperties;
    private static String conteudoEnvExample;

    private static final Path CAMINHO_APPLICATION_PROPERTIES = Path.of("src/main/resources/application.properties");
    private static final Path CAMINHO_ENV_EXAMPLE = Path.of(".env.example");

    private static final Pattern PADRAO_VARIAVEL_AMBIENTE = Pattern.compile("\\$\\{([A-Z_0-9]+)(?::([^}]*))?}");

    private static final PropertyPlaceholderHelper RESOLVER_COM_FALHA =
            new PropertyPlaceholderHelper("${", "}", ":", false);

    @BeforeAll
    static void setUp() throws IOException {
        conteudoApplicationProperties = Files.readString(CAMINHO_APPLICATION_PROPERTIES);
        conteudoEnvExample = Files.readString(CAMINHO_ENV_EXAMPLE);
    }

    // ==================== APPLICATION.PROPERTIES — EXISTÊNCIA ====================

    @Test
    @DisplayName("Deve existir o arquivo application.properties")
    void deveExistirArquivoApplicationProperties() {
        assertThat(CAMINHO_APPLICATION_PROPERTIES).exists();
    }

    // ==================== APPLICATION.PROPERTIES — PORTA ====================

    @Test
    @DisplayName("Deve externalizar a porta do servidor com default 8082")
    void deveExternalizarPortaDoServidorComDefault() {
        assertThat(conteudoApplicationProperties).contains("${SERVER_PORT:8082}");
    }

    // ==================== APPLICATION.PROPERTIES — BANCO DE DADOS ====================

    @Test
    @DisplayName("Deve decompor URL do banco em DB_HOST, DB_PORT e DB_NAME")
    void deveDecomporUrlDoBancoEmVariaveis() {
        assertThat(conteudoApplicationProperties).contains("${DB_HOST:localhost}");
        assertThat(conteudoApplicationProperties).contains("${DB_PORT:5433}");
        assertThat(conteudoApplicationProperties).contains("${DB_NAME:property_catalog_db}");
    }

    @Test
    @DisplayName("Não deve conter URL do banco hardcoded")
    void naoDeveConterUrlDoBancoHardcoded() {
        assertThat(conteudoApplicationProperties)
                .doesNotContain("jdbc:postgresql://localhost:5433/property_catalog_db");
    }

    @Test
    @DisplayName("DB_PASSWORD não deve ter valor default (obrigatório)")
    void dbPasswordNaoDeveTerDefault() {
        assertThat(conteudoApplicationProperties).contains("${DB_PASSWORD}");
        assertThat(conteudoApplicationProperties).doesNotContain("${DB_PASSWORD:");
    }

    @Test
    @DisplayName("URL do banco deve seguir formato JDBC correto com variáveis decompostas")
    void urlDoBancoDeveSegurFormatoJdbcCorreto() {
        assertThat(conteudoApplicationProperties)
                .containsPattern("jdbc:postgresql://\\$\\{DB_HOST[^}]*}:\\$\\{DB_PORT[^}]*}/\\$\\{DB_NAME[^}]*}");
    }

    // ==================== APPLICATION.PROPERTIES — SEGURANÇA ====================

    @Test
    @DisplayName("Não deve conter senhas em texto plano no application.properties")
    void naoDeveConterSenhasEmTextoPlano() {
        assertThat(conteudoApplicationProperties).doesNotContain("arremateai123");
    }

    @Test
    @DisplayName("Não deve exibir SQL em logs de produção (segurança)")
    void naoDeveMostrarSqlEmLogs() {
        assertThat(conteudoApplicationProperties).contains("spring.jpa.show-sql=false");
    }

    // ==================== APPLICATION.PROPERTIES — VARIÁVEIS EXTERNALIZADAS ====================

    @ParameterizedTest
    @ValueSource(strings = {
            "SERVER_PORT",
            "DB_HOST",
            "DB_PORT",
            "DB_NAME",
            "DB_USERNAME",
            "DB_PASSWORD",
            "LOG_LEVEL_APP"
    })
    @DisplayName("Deve externalizar variável no application.properties")
    void deveExternalizarVariavel(String variavel) {
        assertThat(conteudoApplicationProperties)
                .as("Variável %s deve estar externalizada no application.properties", variavel)
                .contains("${" + variavel);
    }

    // ==================== ACTUATOR ====================

    @Test
    @DisplayName("Deve ter actuator expondo apenas endpoint de health")
    void deveTerActuatorExpondoApenasHealth() {
        assertThat(conteudoApplicationProperties).contains("management.endpoints.web.exposure.include=health");
    }

    @Test
    @DisplayName("Não deve expor detalhes do health check (segurança)")
    void naoDeveExporDetalhesDoHealthCheck() {
        assertThat(conteudoApplicationProperties).contains("management.endpoint.health.show-details=never");
    }

    // ==================== .ENV.EXAMPLE — EXISTÊNCIA ====================

    @Test
    @DisplayName("Deve existir o arquivo .env.example")
    void deveExistirArquivoEnvExample() {
        assertThat(CAMINHO_ENV_EXAMPLE).exists();
    }

    // ==================== .ENV.EXAMPLE — DOCUMENTAÇÃO DAS VARIÁVEIS ====================

    @ParameterizedTest
    @ValueSource(strings = {
            "SERVER_PORT",
            "DB_HOST",
            "DB_PORT",
            "DB_NAME",
            "DB_USERNAME",
            "DB_PASSWORD",
            "LOG_LEVEL_APP"
    })
    @DisplayName("Deve documentar variável de ambiente no .env.example")
    void deveDocumentarVariavelNoEnvExample(String variavel) {
        assertThat(conteudoEnvExample)
                .as("Variável %s deve estar documentada no .env.example", variavel)
                .contains(variavel);
    }

    @Test
    @DisplayName("Todas as variáveis do application.properties devem estar documentadas no .env.example")
    void todasAsVariaveisDevemEstarDocumentadasNoEnvExample() {
        Matcher matcher = PADRAO_VARIAVEL_AMBIENTE.matcher(conteudoApplicationProperties);

        int quantidadeVariaveis = 0;
        while (matcher.find()) {
            String variavel = matcher.group(1);
            quantidadeVariaveis++;
            assertThat(conteudoEnvExample)
                    .as("Variável %s usada no application.properties deve estar no .env.example", variavel)
                    .contains(variavel);
        }

        assertThat(quantidadeVariaveis)
                .as("Deve haver pelo menos uma variável externalizada no application.properties")
                .isGreaterThan(0);
    }

    // ==================== .ENV.EXAMPLE — OBRIGATÓRIOS ====================

    @Test
    @DisplayName("Deve marcar DB_PASSWORD como obrigatório no .env.example")
    void deveMarcarSegredosComoObrigatorios() {
        String blocoDbPassword = extrairLinhaComVariavel(conteudoEnvExample, "DB_PASSWORD");
        assertThat(blocoDbPassword)
                .as("DB_PASSWORD deve estar marcado como OBRIGATÓRIO")
                .containsIgnoringCase("OBRIGATÓRIO");
    }

    // ==================== .ENV.EXAMPLE — SEÇÕES ORGANIZADAS ====================

    @Test
    @DisplayName(".env.example deve ter seções organizadas com cabeçalhos descritivos")
    void envExampleDeveTerSecoesOrganizadas() {
        assertThat(conteudoEnvExample).containsIgnoringCase("Servidor");
        assertThat(conteudoEnvExample).containsIgnoringCase("Banco");
        assertThat(conteudoEnvExample).containsIgnoringCase("Logging");
    }

    // ==================== RESOLUÇÃO DE PROPRIEDADES SPRING ====================

    @ParameterizedTest
    @ValueSource(strings = {"DB_PASSWORD"})
    @DisplayName("Deve falhar ao resolver variável obrigatória quando não definida")
    void deveFalharAoResolverVariavelObrigatoriaQuandoNaoDefinida(String variavel) {
        var propriedadesVazias = new Properties();
        var placeholder = "${" + variavel + "}";

        assertThatThrownBy(() ->
                RESOLVER_COM_FALHA.replacePlaceholders(placeholder, propriedadesVazias::getProperty)
        )
                .as("Variável %s sem default deve causar falha na inicialização do Spring", variavel)
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @CsvSource({
            "SERVER_PORT, 8082",
            "DB_HOST, localhost",
            "DB_PORT, 5433",
            "DB_NAME, property_catalog_db",
            "DB_USERNAME, arremateai",
            "LOG_LEVEL_APP, INFO"
    })
    @DisplayName("Deve usar valor default quando variável opcional não é definida")
    void deveUsarValorDefaultQuandoVariavelOpcionalNaoDefinida(String variavel, String defaultEsperado) {
        var propriedadesVazias = new Properties();
        var placeholder = "${" + variavel + ":" + defaultEsperado + "}";

        var resultado = RESOLVER_COM_FALHA.replacePlaceholders(placeholder, propriedadesVazias::getProperty);

        assertThat(resultado)
                .as("Variável %s deve resolver para o default '%s'", variavel, defaultEsperado)
                .isEqualTo(defaultEsperado);
    }

    @ParameterizedTest
    @CsvSource({
            "SERVER_PORT, 9090",
            "DB_HOST, db.producao.com",
            "DB_PORT, 5432",
            "DB_NAME, property_catalog_producao",
            "DB_USERNAME, admin_producao",
            "DB_PASSWORD, senha-segura-123",
            "LOG_LEVEL_APP, DEBUG"
    })
    @DisplayName("Deve usar valor da variável de ambiente quando definida (override do default)")
    void deveUsarValorDaVariavelDeAmbienteQuandoDefinida(String variavel, String valorDefinido) {
        var propriedades = new Properties();
        propriedades.setProperty(variavel, valorDefinido);

        Matcher matcher = PADRAO_VARIAVEL_AMBIENTE.matcher(conteudoApplicationProperties);
        while (matcher.find()) {
            if (matcher.group(1).equals(variavel)) {
                var placeholder = matcher.group(0);
                var resultado = RESOLVER_COM_FALHA.replacePlaceholders(placeholder, propriedades::getProperty);

                assertThat(resultado)
                        .as("Variável %s definida com '%s' deve sobrescrever o default", variavel, valorDefinido)
                        .isEqualTo(valorDefinido);
                return;
            }
        }
    }

    // ==================== CONSISTÊNCIA .ENV.EXAMPLE x APPLICATION.PROPERTIES ====================

    @ParameterizedTest
    @CsvSource({
            "SERVER_PORT, 8082",
            "DB_HOST, localhost",
            "DB_PORT, 5433",
            "DB_NAME, property_catalog_db",
            "DB_USERNAME, arremateai"
    })
    @DisplayName("Default no .env.example deve ser consistente com application.properties")
    void defaultNoEnvExampleDeveSerConsistente(String variavel, String defaultEsperado) {
        String linhaEnv = conteudoEnvExample.lines()
                .filter(l -> l.startsWith(variavel + "="))
                .findFirst()
                .orElse("");

        assertThat(linhaEnv)
                .as("Variável %s no .env.example deve ter default consistente '%s'", variavel, defaultEsperado)
                .contains(defaultEsperado);
    }

    @Test
    @DisplayName("Todas as variáveis sem default devem estar marcadas como obrigatórias no .env.example")
    void todasVariaveisSemDefaultDevemEstarMarcadasComoObrigatorias() {
        Matcher matcher = PADRAO_VARIAVEL_AMBIENTE.matcher(conteudoApplicationProperties);

        while (matcher.find()) {
            String variavel = matcher.group(1);
            String defaultValue = matcher.group(2);

            if (defaultValue == null) {
                String linhasEnv = extrairLinhaComVariavel(conteudoEnvExample, variavel);
                assertThat(linhasEnv.toLowerCase())
                        .as("Variável obrigatória %s (sem default) deve ter indicação no .env.example", variavel)
                        .isNotEmpty();
            }
        }
    }

    // ==================== AUXILIARES ====================

    private String extrairLinhaComVariavel(String conteudo, String variavel) {
        return conteudo.lines()
                .filter(linha -> linha.contains(variavel))
                .reduce("", (a, b) -> a + "\n" + b);
    }
}
