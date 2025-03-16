package me.dio.ui;

import lombok.AllArgsConstructor;
import me.dio.persistence.entity.BoardColumnEntity;
import me.dio.persistence.entity.BoardEntity;
import me.dio.persistence.entity.CardEntity;
import me.dio.service.BoardColumnQueryService;
import me.dio.service.BoardQueryService;
import me.dio.service.CardQueryService;
import me.dio.service.CardService;

import java.sql.SQLException;
import java.util.Scanner;

import static me.dio.persistence.config.ConnectionConfig.getConnection;
import static me.dio.persistence.entity.BoardColumnKindEnum.INITIAL;

@AllArgsConstructor
public class BoardMenu {
    private final Scanner scanner = new Scanner(System.in).useDelimiter("\n");
    private final BoardEntity entity;


    public void execute() {
       try{
        System.out.printf("Bem vindo ao board %s, selecione a opção desejada", entity.getId());
        var option = -1;
        while (option != 9){
            System.out.println("1. Criar um novo card.");
            System.out.println("2. Mover um card");
            System.out.println("3. Boquear um card");
            System.out.println("4. Desbloquear um card");
            System.out.println("5. Cancelar um card");
            System.out.println("6. Visualizar board");
            System.out.println("7. Ver board com cards");
            System.out.println("8. Ver card");
            System.out.println("9. Voltar para o menu anterior");
            System.out.println("0. Sair");
            option = scanner.nextInt();

            switch (option){
                case 1 -> createCard();
                case 2 -> moveCardToNextColumn();
                case 3 -> blockCard();
                case 4 -> unblockCard();
                case 5 -> cancelCard();
                case 6 -> showBoard();
                case 7 -> showColumn();
                case 8 -> showCard();
                case 9 -> System.out.println("Voltando para o menu anterior...");
                case 0 -> System.exit(0);
                default -> System.out.println("Opção inválida.");
            }
        }
    } catch (SQLException ex){
           ex.printStackTrace();
           System.exit(0);
       }
    }

    private void createCard() throws SQLException{
        var card = new CardEntity();
        System.out.println("Informe o título do card");
        scanner.nextLine();
        card.setTitle(scanner.nextLine());

        System.out.println("Informe a descrição do card");
        card.setDescription(scanner.nextLine());

        card.setBoardColumn(entity.getInitialColumn());
        try(var connection = getConnection()){
            System.out.println("Inserindo card no banco...");
            new CardService(connection).insert(card);
            System.out.println("Card inserido com sucesso!");
        }
    }

    private void moveCardToNextColumn() {
    }

    private void blockCard() {
    }

    private void unblockCard() {
    }

    private void cancelCard() {
    }

    private void showBoard() throws SQLException{
        try (var connection = getConnection()){
            var optional = new BoardQueryService(connection).showBoardDetails(entity.getId());
            optional.ifPresent(b -> {
                System.out.printf("Board [%s, %s]\n", b.id(), b.name());
                b.columns().forEach(c ->
                        System.out.printf("Coluna [%s} tipo: [%s] tem %s cards\n", c.name(), c.kind(), c.cardsAmount()));
            });
        }

    }

    private void showColumn() throws SQLException{
        var columnIds = entity.getBoardColumns().stream().map(BoardColumnEntity::getId).toList();
        var selectedColumn = -1L;
        while (columnIds.contains(selectedColumn)){
            System.out.printf("Escolha uma coluna do board %s\n", entity.getName());
            entity.getBoardColumns().forEach(c -> System.out.printf("%s - %s [%s]\n", c.getId(), c.getName(), c.getKind()));
            selectedColumn = scanner.nextLong();
        }
        try(var connection = getConnection()){
            var column = new BoardColumnQueryService(connection).findById(selectedColumn);
            column.ifPresent(co -> {
                System.out.printf("Coluna %s tipo %s \n", co.getName(), co.getKind());
                co.getCards().forEach(ca -> System.out.printf("Card %s - %s\nDescrição: %s\n",
                        ca.getId(), ca.getTitle(), ca.getDescription()));
            });
        }
    }

    private void showCard() throws SQLException{
        System.out.println("Informe o id do card que deseja visualizar");
        var selectedCardId = scanner.nextLong();
        try (var connection = getConnection()){
            new CardQueryService(connection).findById(selectedCardId)
                    .ifPresentOrElse(
                            c -> {
                                System.out.printf("Card %s - %s\n", c.id(), c.title());
                                System.out.printf("Descrição %s\n", c.description() );
                                System.out.println(c.blocked() ? "Está bloqueeado. Motivo: " + c.blockReason() : "Não está bloqueado");
                                System.out.printf("Já foi bloqueado %s vezes\n", c.blockedAmount());
                                System.out.printf("No momento, está na coluna %s - %s\n", c.columnId(), c.columnName());
                            },
                            () -> System.out.printf("Não existe um card com o id %s\n", selectedCardId));

        }
    }
}
