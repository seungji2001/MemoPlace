package ddwu.mobile.finalproject;

import android.nfc.Tag;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

public class LocationXmlParser {
    public boolean first_title = true;
    private enum TagType{NONE, TITLE, ADDRESS};

    private final static String FAULT_RESULT = "faultResult";
    private final static String ITEM_TAG = "item";
    private final static String TITLE_TAG = "title";
    private final static String ADDRESS_TAG = "address";

    private XmlPullParser parser;

    public LocationXmlParser(){
        XmlPullParserFactory factory = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            parser = factory.newPullParser();
        }catch (XmlPullParserException e){
            e.printStackTrace();
        }
    }

    public ArrayList<SearchPlace> parse(String xml){
        Log.d("TAG",xml);
        ArrayList<SearchPlace> placeArrayList = new ArrayList<>();
        SearchPlace sp = null;
        TagType tagType = TagType.NONE;

        try {
            parser.setInput(new StringReader(xml));
            int eventType = parser.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT){
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        String tag = parser.getName();
                        Log.d("TAG",tag);
                        if(tag.equals(ITEM_TAG)){
                            sp = new SearchPlace();
                        }else if(tag.equals(TITLE_TAG)&&first_title) {
                            first_title = false;
                        }else if(tag.equals(TITLE_TAG)&&!first_title){
                            tagType = TagType.TITLE;
                            first_title = true;
                        }else if(tag.equals(ADDRESS_TAG)){
                            tagType = TagType.ADDRESS;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if(parser.getName().equals(ITEM_TAG)){
                            placeArrayList.add(sp);
                        }
                        break;
                    case XmlPullParser.TEXT:
                        switch (tagType){
                            case TITLE:
                                sp.setTitle(parser.getText());
                                break;
                            case ADDRESS:
                                sp.setAddress(parser.getText());
                                break;
                        }
                        tagType = TagType.NONE;
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return placeArrayList;
    }
}
