package com.example.streetinkbookingsystem.services;

import com.example.streetinkbookingsystem.models.TattooArtist;
import com.example.streetinkbookingsystem.repositories.TattooArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TattooArtistService {

    @Autowired
    private TattooArtistRepository tattooArtistRepository;

    /**
     * @author Tara
     * @return viser en liste med alle tatovørene
     */
    public List<TattooArtist> showTattooArtist(){
        return tattooArtistRepository.showTattooArtists();
    }

    public TattooArtist getTattooArtistByUsername(String username) {
        return tattooArtistRepository.getTattooArtistByUsername(username);
    }

    public void deleteProfileByUsername(String profileToDelete) {
    }

    public String changeAdminStatus(TattooArtist artist) {
        boolean isAdmin = artist.getIsAdmin();
        List<TattooArtist> artists = tattooArtistRepository.showTattooArtists();
        int adminCount = 0;

        for (TattooArtist currentArtist : artists) {
            if (currentArtist.getIsAdmin()) {
                adminCount++;
            }
        }

        if (!isAdmin) {
            tattooArtistRepository.changeAdminStatus(artist.getUsername(), true);
            return "Admin status granted";
        } else {
            if (adminCount > 1) {
                tattooArtistRepository.changeAdminStatus(artist.getUsername(), false);
                return "Admin status revoked";
            } else {
                return "At least 1 admin required";
            }
        }
    }


}
