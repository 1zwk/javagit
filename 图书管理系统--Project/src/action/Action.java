package action;

import classes.Record;
import databases.QueryCondition;
import exception.NoSuchBookException;
import classes.Books;
import classes.User;
import databases.BookShelf;
import databases.RecordShelf;
import exception.BookNotEnoughException;
import exception.NotBorrowedException;
import exception.YetBorrowedException;


import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class Action {
    public static Books putBook(String ISBN, String title, String writer, Double price, int count) {
        BookShelf bookShelf = BookShelf.getInstance();
        try {
            Books book = bookShelf.search(ISBN);
            book.increaseCount(count);
            return book;
        } catch (NoSuchBookException exc) {
            Books book = new Books(ISBN, title, writer, price, count);
            bookShelf.putBook(book);
            return book;
        }
    }


    public static List<Books> queryBooks(Comparator<Books> orderBy) {
        BookShelf bookShelf = BookShelf.getInstance();
        return bookShelf.queryBooks(null,orderBy);
    }

    /**
     * 根据条件查询
     * @param queryCondition 查询的条件
     * @return
     */
    public static List<Books> queryBooksByCondition(QueryCondition<Books> queryCondition){
        BookShelf bookShelf = BookShelf.getInstance();
        return bookShelf.queryBooks(queryCondition,null);
    }

    public static List<Record> queryRecord() {
        RecordShelf recordShelf = RecordShelf.getInstance();
        return recordShelf.queryRecord(null);
    }

    /*
  1.书架中是否有该书
  2.该书存量 > 0
  3.这个用户之前没借过该书。
   */
    public static Books borrowBook(User user, String ISBN) throws NoSuchBookException, BookNotEnoughException, YetBorrowedException {
        BookShelf bookShelf = BookShelf.getInstance();
        RecordShelf recordShelf = RecordShelf.getInstance();
        Books book = bookShelf.search(ISBN);
        if (book.getCurCount() == 0) {
            throw new BookNotEnoughException();
        }
        if (recordShelf.contains(user, ISBN)) {
            throw new YetBorrowedException();
        }
        book.borrowBook();
        recordShelf.put(user, ISBN);
        return book;
    }


    public static Books returnBook(User user, String ISBN) throws NoSuchBookException, NotBorrowedException {
        BookShelf bookShelf = BookShelf.getInstance();
        Books book = bookShelf.search(ISBN);
        RecordShelf recordShelf = RecordShelf.getInstance();
        recordShelf.remove(user, ISBN);
        book.returnBook();
        return book;
    }



}