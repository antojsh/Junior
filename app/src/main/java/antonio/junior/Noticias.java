package antonio.junior;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

public class Noticias extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    SwipeRefreshLayout swipeLayout;
    ListView list;
    //LazyImageLoadAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_noticias, container, false);


        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.layoutnoticias);
        swipeLayout.setOnRefreshListener(this);
      /*list= (ListView) rootView.findViewById(R.id.list);

        adapter= new LazyImageLoadAdapter(this.getActivity(),strings);
        list.setAdapter(adapter);*/
       loadFeed();
        return rootView;
    }

    private void loadFeed() {
        ProgressDialog progress;
        progress = ProgressDialog.show(this.getActivity(), "Cargando",
                "Espera Miestras se cargan las noticias", true);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://juniorfc.co/feed/", new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    String result = new String(responseBody);
                    Log.d("************ ", "TODO SALIO BIEN");
                    parserXML(result);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("************ ","TODO SALIO MAL");
            }

        });
        progress.dismiss();
    }

    private void parserXML(String result) {

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(result));
            String value = null;

            while(xpp.getEventType() !=XmlPullParser.END_DOCUMENT){ // loop from the beginning to the end of the XML document
                String titulo="";
                String Descripcion="";
                if(xpp.getEventType()==XmlPullParser.START_TAG){

                    if(xpp.getName().equals("title")){
                        //Toast.makeText(this.getActivity(), xpp.nextText(), Toast.LENGTH_LONG).show();
                        titulo =xpp.nextText().toString();
                    }
                    if(xpp.getName().equals("encoded")){

                        //String[] separated = android.text.Html.fromHtml(xpp.nextText()).toString().split("Read More");
                        String [] imgTemp = xpp.nextText().toString().split("href=\"");
                        String [] imgFinal = imgTemp[1].split("\">");
                        //Descripcion= separated[0].toString();
                        Toast.makeText(this.getActivity(),imgFinal[0], Toast.LENGTH_LONG).show();
                    }


                }
                else if(xpp.getEventType()==XmlPullParser.END_TAG){

                    // ... end of the tag: </version> in our example
                }
                else if(xpp.getEventType()==XmlPullParser.TEXT){ // in a text node
                    value = xpp.getText(); // here you get the "1" value
                }

                xpp.next(); // next XPP state
            }
        } catch (XmlPullParserException e) {

            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(false);
            }
        }, 5000);
    }


        }