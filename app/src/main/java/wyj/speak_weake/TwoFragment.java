package wyj.speak_weake;


import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class TwoFragment extends Fragment {

    Button hujiu,baojing;
    TextView yichang;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_two_fragment, container, false);
        hujiu=(Button)view.findViewById(R.id.hujiu);
        baojing=(Button)view.findViewById(R.id.baojing);
        yichang=(TextView)view.findViewById(R.id.yichang);

       hujiu.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent();
               intent.setAction("android.intent.action.CALL");
               intent.setData(Uri.parse("tel:"+"15755081825"));
               startActivity(intent);
           }
       });
        baojing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.CALL");
                intent.setData(Uri.parse("tel:"+"110"));
                startActivity(intent);
            }
        });
        return view;
    }
}
