package me.dio.ui;

import me.dio.persistence.entity.BoardColumnEntity;
import me.dio.persistence.entity.BoardColumnKindEnum;
import me.dio.persistence.entity.BoardEntity;
import me.dio.service.BoardQueryService;
import me.dio.service.BoardService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static me.dio.persistence.config.ConnectionConfig.getConnection;
import static me.dio.persistence.entity.BoardColumnKindEnum.*;

public class MainMenu {
    private final Scanner scanner = new Scanner(System.in);

    public void execute() throws SQLException {
        System.out.println("BEM VINDO AO GERENCIADOR DE BOARDS\nEscolha a opção desejada.");
        var option = -1;
        while (true){
            System.out.println("1. Criar um novo board.");
            System.out.println("2. Selecionar um board existente");
            System.out.println("3. Excluir um board");
            System.out.println("4. Sair");
            option = scanner.nextInt();

            switch (option){
                case 1 -> createBoard();
                case 2 -> selectBoard();
                case 3 -> deleteBoard();
                case 4 -> System.exit(0);
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    public void createBoard() throws SQLException{
        var entity = new BoardEntity();
        System.out.println("Informe o nome do board");
        entity.setName(scanner.next());

        System.out.println("Informe o número de colunas extras. Caso queira manter, digite 0");
        var additionalColumns = scanner.nextInt();

        List<BoardColumnEntity> columns = new ArrayList<>();

        System.out.println("Informe o nome da coluna inicial");
        var initialColumnName = scanner.next();
        var initialColumn = createColumn(initialColumnName, INITIAL, 0);
        columns.add(initialColumn);
        for (int i = 0; i < additionalColumns; i++) {
            System.out.println("Informe o nome da coluna de pendências");
            var pendingColumnName = scanner.next();
            var pendingColumn = createColumn(pendingColumnName, PENDING, i + 1);
            columns.add(pendingColumn);
        }

        System.out.println("Informe o nome da coluna final");
        var finalColumnName = scanner.next();
        var finalColumn = createColumn(finalColumnName, PENDING, additionalColumns + 1);
        columns.add(finalColumn);

        System.out.println("Informe o nome da coluna de cancelamento");
        var cancelColumnName = scanner.next();
        var cancelColumn = createColumn(cancelColumnName, CANCEL, additionalColumns + 2);
        columns.add(cancelColumn);

        entity.setBoardColumns(columns);
        try(var connection = getConnection()){
            var service = new BoardService(connection);
            service.insert(entity);
        }
    }

    public void selectBoard() throws SQLException {
        System.out.println("Informe o ID do board a ser selecionado");
        var id = scanner.nextLong();
        try(var connection = getConnection()){
            var queryService = new BoardQueryService(connection);
            var optional  = queryService.findById(id);
            optional.ifPresentOrElse(
                    b -> new BoardMenu(b).execute(),
                    () -> System.out.printf("Não foi encontrado o board com id %s\n", id));

        }
    }

    public void deleteBoard() throws SQLException {
        System.out.println("Informe o ID do board que será excluído");
        var id = scanner.nextLong();
        try(var connection = getConnection()){
            var service = new BoardService(connection);
            if (service.delete(id)){
                System.out.printf("O board %s foi excluído\n", id);
            } else {
                System.out.printf("O board %s não foi encontrado\n", id);
            }

        }
    }

    public BoardColumnEntity createColumn(final String name,
                                          final BoardColumnKindEnum kind,
                                          final int order){
        var boardColumn = new BoardColumnEntity();
        boardColumn.setName(name);
        boardColumn.setKind(kind);
        boardColumn.setOrder(order);
        return boardColumn;
    }
}
