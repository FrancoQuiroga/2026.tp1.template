package main.java.com.bibliotech.model;

public record Ebook(String isbn,String titulo,String autor,int anio,
                    Categoria categoria,String formatoArchivo,double tamanioArchivo) {}
