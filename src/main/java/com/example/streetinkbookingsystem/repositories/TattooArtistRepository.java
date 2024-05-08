package com.example.streetinkbookingsystem.repositories;

import com.example.streetinkbookingsystem.models.TattooArtist;
import org.springframework.beans.factory.annotation.Autowired;
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
}