package com.lnstow.jungle0.util;

import com.lnstow.jungle0.BaseJungle;
import com.lnstow.jungle0.bean.MovieDetailBean;
import com.lnstow.jungle0.bean.MovieListItemBean;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class JsoupUtil {
    static boolean safeMode = true;
    static String safeImage = "https://i0.hdslb.com/bfs/face/member/noface.jpg";

    public static ArrayList<MovieListItemBean> handleListWithJsoup(String html) {
        ArrayList<MovieListItemBean> list = new ArrayList<>(128);
        Document document = Jsoup.parse(html, BaseJungle.JUNGLE_URL);
        Element h2 = document.selectFirst(".sub-h2");
        if (h2 != null) return list;
        Elements elements = document.select(".mfsmall");
        MovieListItemBean item;
        String link;
        for (Element element : elements) {
            item = new MovieListItemBean();
            link = element.parent().absUrl("href");
//            String link = element.parent().attr("href");
            item.setNextLink(link);
            if (safeMode) {
                int index = link.indexOf("&is=", BaseJungle.JUNGLE_LENGTH);
                index = link.indexOf('-', index + 4);
                item.setSid(link.substring(index, link.indexOf("&t=", BaseJungle.JUNGLE_LENGTH)));
                item.setImageLink(safeImage);
                item.setTitle("safe");
            } else {
                item.setSid(link.substring(
                        link.indexOf("&is=", BaseJungle.JUNGLE_LENGTH) + 4,
                        link.indexOf("&t=", BaseJungle.JUNGLE_LENGTH)));
                item.setImageLink(element.attr("src"));
                item.setTitle(element.attr("alt"));
            }
            list.add(item);
        }
        return list;
    }

    public static MovieDetailBean handleDetailWithJsoup(String html) {
        MovieDetailBean detail = new MovieDetailBean();
        Document document = Jsoup.parse(html, BaseJungle.JUNGLE_URL);
        Element element = document.selectFirst(".mfbig");
//        Elements elements = document.select("img[src$='ps.jpg']");
        if (safeMode) detail.setBigImageLink(safeImage);
        else
            detail.setBigImageLink(element.attr("src"));
        element = element.parent().nextElementSibling();
        if (element.child(0).normalName().equals("br")) {
            ArrayList<String> smallImageLink = detail.getSmallImageLink();
            ArrayList<String> smallToBigImage = detail.getSmallToBigImage();
            smallToBigImage.add(detail.getBigImageLink());
            Elements children = element.children();
            Element child;
            int childrenSize = children.size();
            String imageLink;
            char[] oldLink;
            int size;
            int index;
            char[] newLink;
            for (int i = 1; i < childrenSize; i++) {
                child = children.get(i);
                imageLink = child.attr("src");
                if (safeMode) {
                    smallImageLink.add(safeImage);
                    smallToBigImage.add(safeImage);
                    continue;
                } else
                    smallImageLink.add(imageLink);
                oldLink = imageLink.toCharArray();
                size = oldLink.length;
                if (i < 10) index = size - 6;
                else index = size - 7;
                newLink = new char[size + 2];
                System.arraycopy(oldLink, 0, newLink, 0, index);
                newLink[index++] = 'j';
                newLink[index++] = 'p';
                System.arraycopy(oldLink, index - 2, newLink, index, size + 2 - index);
                smallToBigImage.add(String.valueOf(newLink));
            }
            element = element.nextElementSibling();
        }
        if (element.child(0).normalName().equals("br")) {
            char[] oldLink = element.childNode(2).childNode(0).attr("src").toCharArray();
            int size = oldLink.length;
            int index = size - 7;
            char[] newLink = new char[size - 1];
            System.arraycopy(oldLink, 0, newLink, 0, index);
            System.arraycopy(oldLink, index + 1, newLink, index, size - 1 - index);
            if (safeMode) detail.setVideoLink(
                    "https://overwatch.nosdn.127.net/1/assets/img/pages/home/anniversary/header.mp4");
            else
                detail.setVideoLink(String.valueOf(newLink));
//            detail.setVideoLink(element.child(1).child(0).attr("src"));
            element = element.nextElementSibling();
        }
        Element child;
        Elements children;
        int childrenSize;
        ArrayList<String> textKey = detail.getTextKey();
        ArrayList<String[]> textValue = detail.getTextValue();
        ArrayList<String[]> textLink = detail.getTextLink();
        String infoKey;
        String[] infoValue;
        String[] infoLink;
        String value;
        while (true) {
            children = element.children();
            childrenSize = children.size();
            if (childrenSize == 0) break;
//            if (child.normalName().equals("h2")) break;
            infoKey = ((TextNode) element.childNode(0)).getWholeText();
            infoValue = new String[childrenSize];
            infoLink = new String[childrenSize];
            for (int i = 0; i < childrenSize; i++) {
                child = children.get(i);
                infoLink[i] = child.absUrl("href");
                value = ((TextNode) child.childNode(0)).getWholeText();
                if (safeMode) {
                    value = value.substring(value.length() - 1);
                    infoValue[i] = value;
                    continue;
                }
                if (value.charAt(value.length() - 1) == ')')
                    infoValue[i] = value.substring(0, value.indexOf('('));
                else
                    infoValue[i] = value;
            }
            if (safeMode)
                textKey.add(infoKey.substring(infoKey.length() - 3, infoKey.length() - 2));
            else
                textKey.add(infoKey.substring(0, infoKey.length() - 2));
            textValue.add(infoValue);
            textLink.add(infoLink);
            element = element.nextElementSibling();
        }
        return detail;
    }

    public static int getMaxPage(String html) {
        Document document = Jsoup.parse(html, BaseJungle.JUNGLE_URL);
        Element element = document.select("tr").last()
                .previousElementSibling().previousElementSibling();
        String totalSize = element.wholeText();
        return Integer.parseInt(totalSize.substring(
                totalSize.indexOf('/') + 2, totalSize.lastIndexOf(' ')));
    }

}
