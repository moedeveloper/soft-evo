package android.app.printerapp.viewer;


import android.app.printerapp.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DataTextAdapter extends BaseAdapter {

    private Context context;
    private String[] texts = {"aaa", "bbb", "ccc", "ddd", "eee", "fff", "eee", "hhh", "iii", "aaa", "bbb", "ccc", "ddd", "eee", "fff", "eee", "hhh", "iii","aaa", "bbb", "ccc", "ddd", "eee", "fff", "eee", "hhh", "iii","aaa", "bbb", "ccc", "ddd", "eee", "fff", "eee", "hhh", "iii"};

    public DataTextAdapter(Context context) {
        this.context = context;
    }

    public DataTextAdapter(String[] texts, Context context){
        this(context);
        this.texts= texts;
    }

    public int getCount() {
        return texts.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View dataTextView;

        TextView title;
        TextView value;

        if (convertView == null) {
            dataTextView = LayoutInflater.from(context).inflate(R.layout.data_text_view, null);
        } else {
            dataTextView = convertView;
        }

        title = (TextView) dataTextView.findViewById(R.id.data_title_textview);
        value = (TextView) dataTextView.findViewById(R.id.data_value_textview);
        title.setText(texts[position]);
        value.setText(": Value");

        return dataTextView;
    }
}