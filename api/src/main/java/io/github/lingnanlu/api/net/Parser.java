package io.github.lingnanlu.api.net;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import io.github.lingnanlu.api.BuildConfig;
import io.github.lingnanlu.model.Book;
import io.github.lingnanlu.model.BookAbstract;
import io.github.lingnanlu.model.SearchResultMetaInfo;

/**
 * Created by Administrator on 2015/12/28.
 */
public class Parser {

    private static final String TAG = "Parser";

    public static SearchResultMetaInfo parseMetaInfo(String html) {

        Document document = Jsoup.parse(html);

        SearchResultMetaInfo searchResultMetaInfo = new SearchResultMetaInfo();

        Element metaData = document.select(".browseHeaderData").first();

        String content = metaData.text();

        int leftBracketIndex = content.indexOf('(');
        int minusIndex = content.indexOf('-');
        int gongIndex = content.indexOf('共');
        int rightBracketIndex = content.length() - 1;

        int begin = Integer.parseInt(content.substring(leftBracketIndex + 1, minusIndex));
        int end = Integer.parseInt(content.substring(minusIndex + 1, gongIndex - 1));
        int totalCount = Integer.parseInt(content.substring(gongIndex + 2, rightBracketIndex));

        searchResultMetaInfo.setBegin(begin);
        searchResultMetaInfo.setEnd(end);
        searchResultMetaInfo.setTotalCount(totalCount);

        return searchResultMetaInfo;

    }

    public static ArrayList<BookAbstract> parseBookAbstracts(String html) {

        ArrayList<BookAbstract> bookAbstracts = new ArrayList<>();

        Document document = Jsoup.parse(html);

        Elements books = document.select(".briefCitRow");


        if(books.size() != 0) {
            for(Element book : books) {

                BookAbstract bookAbstract = new BookAbstract();
                bookAbstract.setImageUrl(book.select("td.briefcitExtras img").last().attr("src"));

                Element briefcitDetail = book.select("td.briefcitDetail").first();

                bookAbstract.setUrl(briefcitDetail.select("a").first().attr("href"));
                bookAbstract.setBookTitle(briefcitDetail.select("a").first().text());

            /*
             * 因为Html文件中，这部分内容是三个嵌套的span，所以需要对所需要的内容进行计算
             * 从这里也可以看到多层次结构的弊端
             */
                Element level1 = briefcitDetail.select("> .briefcitDetail").first();
                Element level2 = level1.select("> .briefcitDetail").first();
                Element level3 = level2.select("> .briefcitDetail").first();

                String string1 = level1.text();
                String string2 = level2.text();
                String string3 = level3.text();

                bookAbstract.setAuthor(string1.substring(0, string1.length() - string2.length()));
                bookAbstract.setPress(string2.substring(0, string2.length() - string3.length()));

                bookAbstracts.add(bookAbstract);
            }

            return bookAbstracts;
        }

        return null;

    }

    public static Book parseBookDetail(String html) {

        Book book = new Book();

        Document document = Jsoup.parse(html);
        /*
         * 获得书籍基本信息
         */
        Element bibInfoEntry = document.select(".bibInfoEntry").first();
        Element tbody = bibInfoEntry.select("tbody").first();
        Elements trs = tbody.select("tr");
        for(Element tr : trs) {


            String name = tr.select("td").first().text();
            String value = tr.select("td").last().text();

            if(BuildConfig.DEBUG) {
                //System.out.println( name + " : " + value);
            }
            switch (name) {
                case "题名":
                    book.setTitle(value.split(";")[0]);
                    break;
                case "统一题名":
                    break;
                case "索书号":
                    book.setCallNumber(value);
                    break;
                case "主要責任者":
                    book.setAuthor(value);
                    break;
                case "出版发行":
                    book.setPress(value);
                    break;
                case "ISBN":
                    book.setISBN(value.split(" ")[0]);
                    break;
                default:
                    break;

            }
        }

        /*
         * 获得馆藏信息
         */
        Element bibItems = document.select(".bibItems").first();

        Elements bibItemsEntrys = bibItems.select(".bibItemsEntry");

        ArrayList<String[]> storeEntrys = new ArrayList<>();

        for(Element entry : bibItemsEntrys) {

            Elements tds = entry.select("td");
            String[] strs = new String[3];
            for(int i = 0; i < tds.size(); i++) {

                //remove "&nbsp"
                strs[i] = tds.get(i).text().replace("\u00a0","");

            }
            storeEntrys.add(strs);
        }
        book.setStoreInfos(storeEntrys);

        /*
         * 设置封面图片url
         */

        Element img = document.select("[alt=书的封面]").first();
        book.setImgUrl(img.attr("src"));

        return book;
    }
}
