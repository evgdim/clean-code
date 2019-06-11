package com.github.evgdim.restest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;


public class Streams {
    public static void main( String[] args ) {
        List<Book> books = Arrays.asList(
                new Book("a", new Author("Ivanov", 50)),
                new Book("a", new Author("Pertrov", 65)));

        System.out.println(mapToAuthorNamesDeclrative(books));
        System.out.println(mapToAuthorNamesDeclrative(books));


    }

    private static List<String> mapToAuthorNamesImperative(List<Book> books) {
        List<Author> authors = new ArrayList<>();
        for (Book book : books) {
            Author author = book.getAuthor();
            if(author.getAge() > 50 && !authors.contains(author)){
                authors.add(author);
                if(authors.size() > 15)
                    break;
            }
        }

        List<String> result = new ArrayList<>();
        for (Author author : authors) {
            String name = author.getSurname().toUpperCase();
            if(result.contains(name)) {
                result.add(name);
            }
        }
        return result;
    }

    private static List<String> mapToAuthorNamesDeclrative(List<Book> books) {
        return books.stream()
                .map(Book::getAuthor)
                .filter(author -> author.getAge() > 50)
                .limit(15)
                .map(Author::getSurname)
                .map(String::toUpperCase)
                .distinct()
                .collect(toList());
    }
}

class Book {
    private String name;
    private Author author;

    public Book(String name, Author author) {
        this.name = name;
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public Author getAuthor() {
        return author;
    }
}
class Author {
    private String surname;
    private int age;

    public Author(String surname, int age) {
        this.surname = surname;
        this.age = age;
    }

    public String getSurname() {
        return surname;
    }

    public int getAge() {
        return age;
    }
}