package ddwu.mobile.finalproject;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

public class GeocoderXmlParser {
    private enum TagType{NONE, AREA1, AREA2, AREA3};
    private int nameNum = 0;
    private final static String FAULT_RESULT = "faultResult";
    private final static String ITEM_TAG = "region";
    private final static String AREA1_ITEM_TAG = "area0";
    private final static String AREA2_ITEM_TAG = "area2";
    private final static String AREA3_ITEM_TAG = "area3";
    private final static String NAME_TAG = "name";

    private XmlPullParser parser;

    public GeocoderXmlParser(){
        XmlPullParserFactory factory = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            parser = factory.newPullParser();
        }catch (XmlPullParserException e){
            e.printStackTrace();
        }
    }

    public ArrayList<String> parse(String xml){
        Log.d("TAG",xml);
        ArrayList<String> selectedPlace = new ArrayList<>();
        String place = null;
        GeocoderXmlParser.TagType tagType = GeocoderXmlParser.TagType.NONE;

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
                            selectedPlace = new ArrayList<>();
                        }else if(tag.equals(AREA1_ITEM_TAG)){
                            nameNum = 1;
                            place = null;
                        }else if(tag.equals(NAME_TAG)&&nameNum==1){
                            tagType = TagType.AREA1;
                        }else if(tag.equals(AREA2_ITEM_TAG)){
                            nameNum = 2;
                        }else if(tag.equals(NAME_TAG)&&nameNum==2){
                            tagType = TagType.AREA2;
                        }else if(tag.equals(AREA3_ITEM_TAG)){
                            nameNum = 3;
                        }else if(tag.equals(NAME_TAG)&&nameNum==3){
                            tagType = TagType.AREA3;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if(parser.getName().equals(ITEM_TAG)){
                            selectedPlace.add(place);
                        }
                        break;
                    case XmlPullParser.TEXT:
                        switch (tagType){
                            case AREA1:
                                place = parser.getText();
                                break;
                            case AREA2:
                                place += " " + parser.getText();
                                break;
                            case AREA3:
                                place += " " + parser.getText();
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
        return selectedPlace;
    }
}
