package com.example;

// Fitxer.java
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Fitxer {
    private String nom;
    private byte[] contingut;

    public Fitxer(String nom) {
        this.nom = nom;
    }

    public byte[] getContingut() throws IOException {
        File f = new File(nom);
        if (!f.exists() || !f.isFile()) {
            throw new IOException("Fitxer no trobat: " + nom);
        }
        contingut = Files.readAllBytes(f.toPath());
        return contingut;
    }

    public String getNom() {
        return nom;
    }
}