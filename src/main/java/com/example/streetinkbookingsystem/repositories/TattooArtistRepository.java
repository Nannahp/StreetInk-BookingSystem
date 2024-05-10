package com.example.streetinkbookingsystem.repositories;

import com.example.streetinkbookingsystem.models.TattooArtist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TattooArtistRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<TattooArtist> showTattooArtist(){
        String query = "SELECT * FROM tattoo_artist;";
        RowMapper rowMapper = new BeanPropertyRowMapper(TattooArtist.class);
        return jdbcTemplate.query(query, rowMapper);
    }

    public TattooArtist getTattooArtistFromUsername(String profileUsername) {
        String query = "SELECT * FROM tattoo_artist WHERE username = ?";
        RowMapper<TattooArtist> rowMapper = new BeanPropertyRowMapper<>(TattooArtist.class);
        try {
            return jdbcTemplate.queryForObject(query, rowMapper, profileUsername);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void updatePassword(String username, String password) {
        String query = "UPDATE tattoo_artist SET password=? WHERE username=?";
        jdbcTemplate.update(query, password, username);
    }


}
