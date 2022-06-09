/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.model.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.fileaccess.Adapter;
import dk.dtu.compute.se.pisd.designpatterns.observer.BoardTemplate;
import javafx.scene.control.Alert;
import dk.dtu.compute.se.pisd.roborally.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Repository implements IRepository {
	
	private static final String GAME_GAMEID = "ID";

	private static final String GAME_NAME = "name";
	
	private static final String GAME_CURRENTPLAYER = "currentPlayer";

	private static final String GAME_PHASE = "phase";

	private static final String GAME_STEP = "step";
	
	private static final String PLAYER_PLAYERID = "ID";
	
	private static final String PLAYER_NAME = "playerName";

	private static final String PLAYER_COLOUR = "colour";
	
	private static final String PLAYER_GAMEID = "gameID";
	
	private static final String PLAYER_POSITION_X = "posX";

	private static final String PLAYER_POSITION_Y = "posY";

	private static final String PLAYER_HEADING = "heading";

	private DatabaseConnection connector;
	
	public Repository(DatabaseConnection connector){
		this.connector = connector;
	}

	@Override
	public boolean createGameInDB(Board game) {
		if (game.getGameID() == 0) {
			Connection connection = connector.getConnection();
			try {
				connection.setAutoCommit(false);

				PreparedStatement ps = getInsertGameStatementRGK();

				if (game.getName() != null) {
					ps.setString(1, game.getName()); // instead of name
				} else {
					ps.setString(1, "Date: " + new Date()); // instead of name
				}

				ps.setNull(2, Types.TINYINT); // game.getPlayerNumber(game.getCurrentPlayer())); is inserted after players!
				ps.setString(3, game.getPhase().toString());
				ps.setInt(4, game.getStep());
				ps.setString(5, new BoardTemplate().fromBoard(game).toString());

				// If you have a foreign key constraint for current players,
				// the check would need to be temporarily disabled, since
				// MySQL does not have a per transaction validation, but
				// validates on a per row basis.
				Statement statement = connection.createStatement();
				statement.execute("SET foreign_key_checks = 0");
				
				int affectedRows = ps.executeUpdate();
				ResultSet generatedKeys = ps.getGeneratedKeys();
				if (affectedRows == 1 && generatedKeys.next()) {
					game.setGameID(generatedKeys.getInt(1));
				}
				generatedKeys.close();
				
				// Enable foreign key constraint check again:
				statement.execute("SET foreign_key_checks = 1");
				statement.close();

				createPlayersInDB(game);

				// since current player is a foreign key, it can oly be
				// inserted after the players are created, since MySQL does
				// not have a per transaction validation, but validates on
				// a per row basis.
				ps = getSelectGameStatementU();
				ps.setInt(1, game.getGameID());

				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					rs.updateInt(GAME_CURRENTPLAYER, game.getCurrentPlayer().no);
					rs.updateRow();
				} else {
					showError("An error occurred while updating the current player in the database");
				}
				rs.close();

				connection.commit();
				connection.setAutoCommit(true);
				return true;
			} catch (SQLException e) {
				showError("An error occurred while saving the game in the database");
				e.printStackTrace();
				System.err.println("Some DB error");
				
				try {
					connection.rollback();
					connection.setAutoCommit(true);
				} catch (SQLException e1) {
					showError("An error occurred while rolling back changes not fully saved in the database");
					e1.printStackTrace();
				}
			}
		} else {
			System.err.println("Game cannot be created in DB, since it has a game id already!");
		}
		return false;
	}
		
	@Override
	public boolean updateGameInDB(Board game) {
		assert game.getGameID() != 0;
		
		Connection connection = connector.getConnection();
		try {
			connection.setAutoCommit(false);

			PreparedStatement ps = getSelectGameStatementU();
			ps.setInt(1, game.getGameID());
			
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				rs.updateInt(GAME_CURRENTPLAYER, game.getCurrentPlayer().getDbNo());
				rs.updateString(GAME_PHASE, game.getPhase().toString());
				rs.updateInt(GAME_STEP, game.getStep());
				rs.updateRow();
			} else {
				showError("An error occurred while updating the game in the database");
			}
			rs.close();

			updatePlayersInDB(game);

            connection.commit();
            connection.setAutoCommit(true);
			return true;
		} catch (SQLException e) {
			showError("An error occurred while updating the game in the database");
			e.printStackTrace();
			System.err.println("Some DB error");
			
			try {
				connection.rollback();
				connection.setAutoCommit(true);
			} catch (SQLException e1) {
				showError("An error occurred while rolling back changes not fully saved in the database");
				e1.printStackTrace();
			}
		}

		return false;
	}
	
	@Override
	public Board loadGameFromDB(int id) {
		Board game;
		try {
			PreparedStatement ps = getSelectGameStatementU();
			ps.setInt(1, id);
			
			ResultSet rs = ps.executeQuery();
			int playerNo = -1;
			if (rs.next()) {

				// TODO refactor this to something nicer. This shouldn't be handled here
				GsonBuilder simpleBuilder = new GsonBuilder().registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>());
				Gson gson = simpleBuilder.create();

				BoardTemplate boardTemplate = gson.fromJson(rs.getString("boardLayout"), BoardTemplate.class);
				game = boardTemplate.toBoard();

				if (game == null) {
					return null;
				}
				playerNo = rs.getInt(GAME_CURRENTPLAYER);
				game.setName(rs.getString(GAME_NAME));
				game.setPhase(Phase.valueOf(rs.getString(GAME_PHASE)));
				game.setStep(rs.getInt(GAME_STEP));
			} else {
				showError("An error occurred while getting the game from the database");
				return null;
			}
			rs.close();

			game.setGameID(id);
			loadPlayersFromDB(game);

			if (playerNo >= 0 && playerNo < game.getPlayersNumber()) {
				game.setCurrentPlayer(game.getPlayer(playerNo));
			} else {
				showError("An error occurred while setting the current player");
				return null;
			}

			return game;
		} catch (SQLException e) {
			showError("An error occurred while getting the game from the database");
			e.printStackTrace();
			System.err.println("Some DB error");
		}
		return null;
	}
	
	@Override
	public List<GameInDB> getGames() {
		List<GameInDB> result = new ArrayList<>();
		try {
			PreparedStatement ps = getSelectGameIdsStatement();
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int id = rs.getInt(GAME_GAMEID);
				String name = rs.getString(GAME_NAME);
				result.add(new GameInDB(id,name));
			}
			rs.close();
		} catch (SQLException e) {
			showError("An error occurred while getting the list of games from the database");
			e.printStackTrace();
		}
		return result;		
	}

	private void createPlayersInDB(Board game) throws SQLException {
		PreparedStatement ps = getSelectPlayersStatementU();
		PreparedStatement ps1 = getCreatePlayerHandStatement();
		PreparedStatement ps2 = getCreatePlayerRegisterStatement();
		ps.setInt(1, game.getGameID());
		
		ResultSet rs = ps.executeQuery();
		for (int i = 0; i < game.getPlayersNumber(); i++) {
			Player player = game.getPlayer(i);
			rs.moveToInsertRow();
			rs.updateInt(PLAYER_GAMEID, game.getGameID());
			rs.updateInt(PLAYER_PLAYERID, player.getDbNo());
			rs.updateString(PLAYER_NAME, player.getName());
			rs.updateString(PLAYER_COLOUR, player.getColor());
			rs.updateInt(PLAYER_POSITION_X, player.getSpace().x);
			rs.updateInt(PLAYER_POSITION_Y, player.getSpace().y);
			rs.updateString(PLAYER_HEADING, player.getHeading().toString());
			rs.updateInt("order",player.no);
			rs.updateInt("last_checkpoint", player.getLastCheckpoint());
			rs.insertRow();

			ps1.setInt(1, player.getDbNo());
			ps1.setInt(2, game.getGameID());
			for (int j = 3; j < 11; j++) {
				CommandCard commandCard = player.getCardField((j-3)).getCard();
				if (commandCard == null) {
					ps1.setNull(j, Types.VARCHAR);
				} else {
					ps1.setString(j, commandCard.command.toString());
				}
			}
			int res = ps1.executeUpdate();

			ps2.setInt(1, player.getDbNo());
			ps2.setInt(2, game.getGameID());
			for (int j = 3; j < 8; j++) {
				CommandCard commandCard = player.getProgramField((j-3)).getCard();
				if (commandCard == null) {
					ps2.setNull(j, Types.VARCHAR);
				} else {
					ps2.setString(j, commandCard.command.toString());
				}
			}
			int res2 = ps2.executeUpdate();
		}

		rs.close();
		ps1.close();
		ps2.close();
	}
	
	private void loadPlayersFromDB(Board game) throws SQLException {
		PreparedStatement ps = getSelectPlayersASCStatement();
		PreparedStatement ps1 = getSelectPlayerHandStatement();
		PreparedStatement ps2 = getSelectPlayerRegisterStatement();
		ps.setInt(1, game.getGameID());
		
		ResultSet rs = ps.executeQuery();
		int i = 0;
		while (rs.next()) {
			int playerId = rs.getInt(PLAYER_PLAYERID);
			if (i++ == playerId) {
				String name = rs.getString(PLAYER_NAME);
				String colour = rs.getString(PLAYER_COLOUR);
				Player player = new Player(game, colour ,name);
				player.setDbNo(playerId);
				player.no = rs.getInt("order");
				player.setLastCheckpoint(rs.getInt("last_checkpoint"));
				game.addPlayer(player);
				
				int x = rs.getInt(PLAYER_POSITION_X);
				int y = rs.getInt(PLAYER_POSITION_Y);
				player.setSpace(game.getSpace(x,y));
				Heading heading = Heading.valueOf(rs.getString(PLAYER_HEADING));
				player.setHeading(heading);

				ps1.setInt(1, playerId);
				ps1.setInt(2, game.getGameID());
				ResultSet rs1 = ps1.executeQuery();

				while (rs1.next()) {
					for (int j = 0; j < 8; j++) {
						Command command = null;
						if (rs1.getString("card" + j) != null) {
							command = Command.valueOf(rs1.getString("card" + j));
						}
						CommandCard card = null;
						if (command != null) {
							card = new CommandCard(command);
						}
						player.getCardField(j).setCard(card);
					}
				}

				ps2.setInt(1, playerId);
				ps2.setInt(2, game.getGameID());
				ResultSet rs2 = ps2.executeQuery();

				while (rs2.next()) {
					for (int j = 0; j < 5; j++) {
						Command command = null;
						if (rs2.getString("card" + j) != null) {
							command = Command.valueOf(rs2.getString("card" + j));
						}
						CommandCard card = null;
						if (command != null) {
							card = new CommandCard(command);
						}
						player.getProgramField(j).setCard(card);
					}
				}


			} else {
				showError("An error occurred while getting a player from the database");
				System.err.println("Game in DB does not have a player with id " + i +"!");
			}
		}
		rs.close();
		ps1.close();
		ps2.close();
	}
	
	private void updatePlayersInDB(Board game) throws SQLException {
		PreparedStatement ps = getSelectPlayersStatementU();
		PreparedStatement ps1 = getSelectPlayerHandStatement();
		PreparedStatement ps2 = getSelectPlayerRegisterStatement();
		ps.setInt(1, game.getGameID());
		
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			int playerId = rs.getInt(PLAYER_PLAYERID);
			Player player = game.getPlayerByDB(playerId);
			rs.updateInt(PLAYER_POSITION_X, player.getSpace().x);
			rs.updateInt(PLAYER_POSITION_Y, player.getSpace().y);
			rs.updateString(PLAYER_HEADING, player.getHeading().toString());
			rs.updateInt("order", player.no);
			rs.updateInt("last_checkpoint", player.getLastCheckpoint());
			rs.updateRow();
		}

		rs = ps.executeQuery();
		while (rs.next()) {
			int playerId = rs.getInt(PLAYER_PLAYERID);
			Player player = game.getPlayerByDB(playerId);

			ps1.setInt(1, playerId);
			ps1.setInt(2, game.getGameID());

			ResultSet resultSet = ps1.executeQuery();

			while (resultSet.next()) {
				for (int i = 0; i < 8; i++) {
					CommandCard card = player.getCardField(i).getCard();
					if (card == null) {
						resultSet.updateNull("card" + i);
					} else {
						resultSet.updateString("card" + i, card.command.toString());
					}
				}
				resultSet.updateRow();
			}

			ps2.setInt(1, playerId);
			ps2.setInt(2, game.getGameID());
			ResultSet resultSet2 = ps2.executeQuery();

			while (resultSet2.next()) {
				for (int i = 0; i < 5; i++) {
					CommandCard card = player.getProgramField(i).getCard();
					if (card == null) {
						resultSet2.updateNull("card" + i);
					} else {
						resultSet2.updateString("card" + i, card.command.toString());
					}
				}
				resultSet2.updateRow();
			}

		}

		rs.close();
		ps1.close();
		ps2.close();
	}

	private static final String SQL_INSERT_GAME =
			"INSERT INTO Game(name, currentPlayer, phase, step, boardLayout) VALUES (?, ?, ?, ?, ?)";

	private PreparedStatement insert_game_stmt = null;

	private PreparedStatement getInsertGameStatementRGK() {
		if (insert_game_stmt == null) {
			Connection connection = connector.getConnection();
			try {
				insert_game_stmt = connection.prepareStatement(
						SQL_INSERT_GAME,
						Statement.RETURN_GENERATED_KEYS);
			} catch (SQLException e) {
				// TODO error handling
				e.printStackTrace();
			}
		}
		return insert_game_stmt;
	}

	private static final String SQL_SELECT_GAME =
			"SELECT * FROM Game WHERE " + GAME_GAMEID + " = ?";
	
	private PreparedStatement select_game_stmt = null;
	
	private PreparedStatement getSelectGameStatementU() {
		if (select_game_stmt == null) {
			Connection connection = connector.getConnection();
			try {
				select_game_stmt = connection.prepareStatement(
						SQL_SELECT_GAME,
						ResultSet.TYPE_FORWARD_ONLY,
					    ResultSet.CONCUR_UPDATABLE);
			} catch (SQLException e) {
				// TODO error handling
				e.printStackTrace();
			}
		}
		return select_game_stmt;
	}
		
	private static final String SQL_SELECT_PLAYERS =
			"SELECT * FROM Player WHERE gameID = ?";

	private PreparedStatement select_players_stmt = null;

	private PreparedStatement getSelectPlayersStatementU() {
		if (select_players_stmt == null) {
			Connection connection = connector.getConnection();
			try {
				select_players_stmt = connection.prepareStatement(
						SQL_SELECT_PLAYERS,
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_UPDATABLE);
			} catch (SQLException e) {
				// TODO error handling
				e.printStackTrace();
			}
		}
		return select_players_stmt;
	}

	private static final String SQL_CREATE_PLAYER_HAND =
			"INSERT INTO playerHand(playerID, gameID, card0, card1, card2, card3, card4, card5, card6, card7) VALUES (?,?,?,?,?,?,?,?,?,?)";

	private PreparedStatement create_player_hand_stmt = null;

	private PreparedStatement getCreatePlayerHandStatement() {
		if (create_player_hand_stmt == null) {
			Connection connection = connector.getConnection();
			try {
				create_player_hand_stmt = connection.prepareStatement(SQL_CREATE_PLAYER_HAND);
			} catch (SQLException e) {
				// TODO error handling
				e.printStackTrace();
			}
		}
		return create_player_hand_stmt;
	}

	private static final String SQL_SELECT_PLAYER_HAND =
			"SELECT * FROM playerHand WHERE playerID = ? AND gameID = ?";

	private PreparedStatement select_player_hand_stmt = null;

	private PreparedStatement getSelectPlayerHandStatement() {
		if (select_player_hand_stmt == null) {
			Connection connection = connector.getConnection();
			try {
				select_player_hand_stmt = connection.prepareStatement(SQL_SELECT_PLAYER_HAND,
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_UPDATABLE);
			} catch (SQLException e) {
				// TODO error handling
				e.printStackTrace();
			}
		}
		return select_player_hand_stmt;
	}

	private static final String SQL_SELECT_PLAYER_REGISTER =
			"SELECT * FROM playerRegister WHERE playerID = ? AND gameID = ?";

	private PreparedStatement select_player_register_stmt = null;

	private PreparedStatement getSelectPlayerRegisterStatement() {
		if (select_player_register_stmt == null) {
			Connection connection = connector.getConnection();
			try {
				select_player_register_stmt = connection.prepareStatement(SQL_SELECT_PLAYER_REGISTER,
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_UPDATABLE);
			} catch (SQLException e) {
				// TODO error handling
				e.printStackTrace();
			}
		}
		return select_player_register_stmt;
	}

	private static final String SQL_CREATE_PLAYER_REGISTER =
			"INSERT INTO playerRegister(playerID, gameID, card0, card1, card2, card3, card4) VALUES (?,?,?,?,?,?,?)";

	private PreparedStatement create_player_register_stmt = null;

	private PreparedStatement getCreatePlayerRegisterStatement() {
		if (create_player_register_stmt == null) {
			Connection connection = connector.getConnection();
			try {
				create_player_register_stmt = connection.prepareStatement(SQL_CREATE_PLAYER_REGISTER);
			} catch (SQLException e) {
				// TODO error handling
				e.printStackTrace();
			}
		}
		return create_player_register_stmt;
	}


	private static final String SQL_SELECT_PLAYERS_ASC =
			"SELECT * FROM Player WHERE gameID = ? ORDER BY `ID` ASC";
	
	private PreparedStatement select_players_asc_stmt = null;
	
	private PreparedStatement getSelectPlayersASCStatement() {
		if (select_players_asc_stmt == null) {
			Connection connection = connector.getConnection();
			try {
				// This statement does not need to be updatable
				select_players_asc_stmt = connection.prepareStatement(
						SQL_SELECT_PLAYERS_ASC);
			} catch (SQLException e) {
				// TODO error handling
				e.printStackTrace();
			}
		}
		return select_players_asc_stmt;
	}
	
	private static final String SQL_SELECT_GAMES =
			"SELECT ID, name FROM Game ORDER BY ID DESC LIMIT 5";

	private PreparedStatement select_games_stmt = null;
	
	private PreparedStatement getSelectGameIdsStatement() {
		if (select_games_stmt == null) {
			Connection connection = connector.getConnection();
			try {
				select_games_stmt = connection.prepareStatement(
						SQL_SELECT_GAMES);
			} catch (SQLException e) {
				// TODO error handling
				e.printStackTrace();
			}
		}
		return select_games_stmt;
	}

	private void showError(String error) {
		Alert alert = new Alert(Alert.AlertType.ERROR, error);
		alert.showAndWait();
	}



}
