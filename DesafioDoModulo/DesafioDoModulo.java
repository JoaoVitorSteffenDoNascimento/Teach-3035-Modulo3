import java.util.Random;
import java.util.Scanner;

public class DesafioDoModulo {

    static final Scanner entrada = new Scanner(System.in);
    static final Random random = new Random();

    static final String[] niveis = {"Fácil", "Médio", "Difícil"};
    static final int[] limites = {50, 100, 200};
    static final int[] tentativas = {10, 7, 5};
    static final int[] pontosBase = {100, 200, 300};

    static final int DESCONTO_TENTATIVA = 10;

    static final int[] historicoPontos = new int[10];
    static final int[] historicoNiveis = new int[10];
    static final int[] historicoModos = new int[10];
    static final boolean[] historicoVitorias = new boolean[10];

    static final int[][] recordes = {
        {-1, -1, -1},
        {-1, -1, -1}
    };

    static int quantidadeHistorico = 0;

    public static void main(String[] args) {
        int opcao;

        do {
            System.out.println(
                "\n=== JOGO DE ADIVINHAÇÃO ===\n"
                + "1 - Iniciar novo jogo\n"
                + "2 - Ver regras\n"
                + "3 - Ver histórico\n"
                + "4 - Ver recordes\n"
                + "5 - Sair"
            );

            opcao = lerInteiro("Escolha: ", 1, 5);

            switch (opcao) {
                case 1:
                    jogar();
                    break;
                case 2:
                    mostrarRegras();
                    break;
                case 3:
                    mostrarHistorico();
                    break;
                case 4:
                    mostrarRecordes();
                    break;
            }
        } while (opcao != 5);

        entrada.close();
        System.out.println("Até a próxima!");
    }

    static int lerInteiro(String mensagem, int minimo, int maximo) {
        while (true) {
            System.out.print(mensagem);

            try {
                int numero = Integer.parseInt(entrada.nextLine().trim());

                if (numero >= minimo && numero <= maximo) {
                    return numero;
                }
            } catch (NumberFormatException e) {
            }

            System.out.println("Digite um número inteiro entre " + minimo + " e " + maximo + ".");
        }
    }

    static void jogar() {
        System.out.println("\nEscolha a dificuldade:\n1 - Fácil\n2 - Médio\n3 - Difícil");

        int nivel = lerInteiro("Dificuldade: ", 1, 3) - 1;

        System.out.println("\nEscolha o modo:\n1 - Normal\n2 - Sequência de 3 números");

        int modo = lerInteiro("Modo: ", 1, 2) - 1;
        int quantidadeNumeros = modo == 0 ? 1 : 3;

        int pontuacaoTotal = 0;
        boolean venceu = true;

        for (int i = 0; i < quantidadeNumeros; i++) {
            int numeroSecreto = random.nextInt(limites[nivel]) + 1;
            int tentativasUsadas = 0;
            int custoDicas = 0;
            int ultimoPalpite = -1;

            boolean acertou = false;
            boolean[] dicasUsadas = new boolean[3];

            System.out.println("\nNúmero " + (i + 1) + " de " + quantidadeNumeros);
            System.out.println("Adivinhe um número entre 1 e " + limites[nivel] + ".");

            while (tentativasUsadas < tentativas[nivel] && !acertou) {
                System.out.println("\nTentativas restantes: " + (tentativas[nivel] - tentativasUsadas));

                System.out.println("1 - Dar palpite\n2 - Pedir dica");

                int acao = lerInteiro("Escolha: ", 1, 2);

                if (acao == 2) {
                    System.out.println(
                        "\n1 - Paridade: -10 pontos\n"
                        + "2 - Metade do intervalo: -20 pontos\n"
                        + "3 - Proximidade: -15 pontos\n"
                        + "4 - Voltar"
                    );

                    int dica = lerInteiro("Dica: ", 1, 4);

                    if (dica == 4) {
                        continue;
                    }

                    if (dicasUsadas[dica - 1]) {
                        System.out.println("Essa dica já foi usada.");
                        continue;
                    }

                    if (dica == 3 && ultimoPalpite == -1) {
                        System.out.println("Dê um palpite primeiro.");
                        continue;
                    }

                    custoDicas += mostrarDica(dica, numeroSecreto, ultimoPalpite, limites[nivel]);

                    dicasUsadas[dica - 1] = true;
                    continue;
                }

                ultimoPalpite = lerInteiro("Seu palpite: ", 1, limites[nivel]);

                tentativasUsadas++;

                if (ultimoPalpite == numeroSecreto) {
                    acertou = true;
                    System.out.println("Você acertou!");
                } else if (ultimoPalpite < numeroSecreto) {
                    System.out.println("O número correto é maior.");
                } else {
                    System.out.println("O número correto é menor.");
                }
            }

            if (!acertou) {
                venceu = false;
                pontuacaoTotal = 0;

                System.out.println("Acabaram as tentativas! O número era " + numeroSecreto + ".");

                break;
            }

            int pontos = calcularPontuacao(nivel, tentativasUsadas, custoDicas);

            pontuacaoTotal += pontos;

            System.out.println("Pontos deste número: " + pontos);
        }

        registrarHistorico(pontuacaoTotal, nivel, modo, venceu);

        if (venceu) {
            System.out.println("\nVocê venceu!");

            if (pontuacaoTotal > recordes[modo][nivel]) {
                recordes[modo][nivel] = pontuacaoTotal;
                System.out.println("Novo recorde neste nível e modo!");
            }
        } else {
            System.out.println("\nVocê perdeu!");
        }

        System.out.println("Pontuação final: " + pontuacaoTotal);
    }

    static int mostrarDica(int dica, int numeroSecreto, int ultimoPalpite, int limite) {
        switch (dica) {
            case 1:
                if (numeroSecreto % 2 == 0) {
                    System.out.println("O número é par.");
                } else {
                    System.out.println("O número é ímpar.");
                }
                return 10;

            case 2:
                int metade = limite / 2;
                if (numeroSecreto <= metade) {
                    System.out.println("Está entre 1 e " + metade + ".");
                } else {
                    System.out.println("Está entre " + (metade + 1) + " e " + limite + ".");
                }
                return 20;

            case 3:
                int distancia = Math.abs(ultimoPalpite - numeroSecreto);
                int limiteQuente = limite / 10;
                if (distancia <= limiteQuente) {
                    System.out.println("Quente! Distância de até " + limiteQuente + ".");
                } else {
                    System.out.println("Frio! Distância maior que " + limiteQuente + ".");
                }
                return 15;

            default:
                return 0;
        }
    }

    static int calcularPontuacao(int nivel, int tentativasUsadas, int custoDicas) {
        int restantes = tentativas[nivel] - tentativasUsadas;

        int pontos = pontosBase[nivel]
            - tentativasUsadas * DESCONTO_TENTATIVA
            + restantes * 50
            - custoDicas;

        return Math.max(0, pontos);
    }

    static void registrarHistorico(int pontos, int nivel, int modo, boolean venceu) {

        if (quantidadeHistorico == historicoPontos.length) {
            for (int i = 1; i < quantidadeHistorico; i++) {
                historicoPontos[i - 1] = historicoPontos[i];
                historicoNiveis[i - 1] = historicoNiveis[i];
                historicoModos[i - 1] = historicoModos[i];
                historicoVitorias[i - 1] = historicoVitorias[i];
            }

            quantidadeHistorico--;
        }

        historicoPontos[quantidadeHistorico] = pontos;
        historicoNiveis[quantidadeHistorico] = nivel;
        historicoModos[quantidadeHistorico] = modo;
        historicoVitorias[quantidadeHistorico] = venceu;

        quantidadeHistorico++;
    }

    static void mostrarHistorico() {
        System.out.println("\n=== HISTÓRICO ===");

        if (quantidadeHistorico == 0) {
            System.out.println("Nenhuma partida registrada.");
            return;
        }

        for (int i = quantidadeHistorico - 1; i >= 0; i--) {
            String modo = historicoModos[i] == 0
                ? "Normal" : "Sequência";

            String resultado = historicoVitorias[i]
                ? "Vitória" : "Derrota";

            System.out.println(
                niveis[historicoNiveis[i]]
                + " | " + modo
                + " | " + resultado
                + " | " + historicoPontos[i] + " pontos"
            );
        }
    }

    static void mostrarRecordes() {
        System.out.println("\n=== RECORDES ===");

        for (int modo = 0; modo < recordes.length; modo++) {
            System.out.println(modo == 0 ? "\nModo normal:" : "\nModo sequência:");

            for (int nivel = 0; nivel < niveis.length; nivel++) {
                if (recordes[modo][nivel] == -1) {
                    System.out.println(niveis[nivel] + ": nenhuma vitória.");
                } else {
                    System.out.println(niveis[nivel] + ": " + recordes[modo][nivel] + " pontos.");
                }
            }
        }
    }

    static void mostrarRegras() {
        System.out.println("\n=== REGRAS ===");

        for (int i = 0; i < niveis.length; i++) {
            System.out.println(
                niveis[i] + ": números de 1 a " + limites[i]
                + ", " + tentativas[i] + " tentativas"
                + ", base de " + pontosBase[i] + " pontos."
            );
        }

        System.out.println(
            "\nCada palpite válido desconta 10 pontos.\n"
            + "O palpite correto também conta como tentativa.\n"
            + "Cada tentativa restante dá um bônus de 50 pontos.\n"
            + "O custo das dicas é descontado da pontuação.\n"
            + "A pontuação mínima é zero. Derrotas valem zero.\n"
            + "Entradas inválidas não gastam tentativas.\n"
            + "Dicas não gastam tentativas.\n"
            + "Cada dica pode ser usada uma vez por número.\n"
            + "Proximidade compara o último palpite ao número.\n"
            + "Quente: distância de até 10% do limite do nível.\n"
            + "Frio: distância maior que esse valor."
        );

        System.out.println(
            "\nNa sequência, acerte três números em ordem.\n"
            + "Os números podem se repetir.\n"
            + "Cada número recebe novas tentativas.\n"
            + "Acertar os três soma suas pontuações.\n"
            + "Falhar em um número zera a pontuação da partida."
        );

        System.out.println(
            "\nO histórico guarda as últimas 10 partidas.\n"
            + "Os recordes são separados por nível e modo.\n"
            + "Histórico e recordes duram enquanto o jogo estiver aberto."
        );
    }
}