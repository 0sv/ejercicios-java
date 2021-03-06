package com.arkaitzgarro.jdbc.model.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.arkaitzgarro.jdbc.db.DataBaseHelper;
import com.arkaitzgarro.jdbc.model.Film;
import com.arkaitzgarro.jdbc.model.interfaces.IFilm;

public class FilmRepository {
	private DataBaseHelper dbHelper;

	public FilmRepository() throws SQLException {
		dbHelper = new DataBaseHelper();
	}

	/**
	 * Buscar todas las peliculas de la base de datos
	 * 
	 * @return
	 */
	public List<IFilm> findAll() {
		List<IFilm> list = new ArrayList<IFilm>();
		String sql = "SELECT film_id, title, description, release_year FROM film;";

		ResultSet rows = dbHelper.query(sql);
		try {
			if (rows != null) {
				while (rows.next()) {
					Film film = new Film();
					film.setId(rows.getLong("film_id"));
					film.setTitle(rows.getString("title"));
					film.setDescription(rows.getString("description"));
					film.setYear(rows.getDate("release_year"));

					list.add(film);
				}

				rows.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * Busca una pelicula por ID
	 * 
	 * @param id
	 * @return
	 */
	public IFilm findOneById(long id) {
		IFilm film = null;

		String sql = "SELECT * FROM film  WHERE film_id = ?;";
		PreparedStatement query = dbHelper.queryPrepared(sql);

		try {
			if (query != null) {
				query.setLong(1, id);

				ResultSet row = query.executeQuery();

				if (row != null && row.next()) {
					film = new Film();
					film.setId(row.getLong("film_id"));
					film.setTitle(row.getString("title"));
					film.setDescription(row.getString("description"));
					film.setYear(row.getDate("release_year"));
				}

				row.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return film;
	}

	/**
	 * Insertar una pelicula en la base de datos
	 * 
	 * @param film
	 * @return
	 */
	public boolean insert(IFilm film) {

		String insert = "INSERT INTO film(title, description, release_year, language_id)"
				+ " VALUES(?, ?, ?, 1);";

		Calendar cal = Calendar.getInstance();
		PreparedStatement query = dbHelper.queryPrepared(insert);

		try {
			if (query != null) {
				query.setString(1, film.getTitle());
				query.setString(2, film.getDescription());
				query.setString(3, String.valueOf(cal.get(Calendar.YEAR)));

				int rows = query.executeUpdate();

				if (rows == 1) {
					ResultSet generatedKeys = query.getGeneratedKeys();
					if (generatedKeys.next()) {
						film.setId(generatedKeys.getLong(1));

						generatedKeys.close();
						query.close();

						return true;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (query != null) {
					query.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	/**
	 * Update an existing film
	 * 
	 * @param film
	 * @return
	 */
	public boolean update(IFilm film) {
		Calendar cal = Calendar.getInstance();

		String sql = "UPDATE film"
				+ " SET title=?, description=?, release_year=?"
				+ " WHERE film_id=?";

		// Preparar la consulta
		PreparedStatement query = dbHelper.queryPrepared(sql);

		try {
			if (query != null) {
				query.setString(1, film.getTitle());
				query.setString(2, film.getDescription());

				query.setString(3, String.valueOf(cal.get(Calendar.YEAR)));
				query.setLong(4, film.getId());

				int rows = query.executeUpdate();

				query.close();

				return rows == 1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Remove a film from DB
	 * 
	 * @param film
	 * @return
	 */
	public boolean delete(IFilm film) {
		String sql = "DELETE FROM film WHERE film_id = ?";

		try {
			PreparedStatement query = dbHelper.queryPrepared(sql);

			if (query != null) {
				query.setLong(1, film.getId());

				int rows = query.executeUpdate();

				query.close();

				return rows == 1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}
}
