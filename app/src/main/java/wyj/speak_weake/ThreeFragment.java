package wyj.speak_weake;


import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.just.agentweb.AgentWeb;

public class ThreeFragment extends Fragment {
    private AgentWeb agentWeb,agentWeb1;
    private LinearLayout linWeb,linWeb1;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_three_fragment, container, false);
        linWeb=view.findViewById(R.id.lin_web);
        linWeb1=view.findViewById(R.id.lin_web1);
        TextView yinyue=view.findViewById(R.id.yinyue);
        TextView story=view.findViewById(R.id.story);
        yinyue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agentWeb=AgentWeb.with(ThreeFragment.this)
                        .setAgentWebParent(linWeb,new LinearLayout.LayoutParams(-1,-1))
                        .useDefaultIndicator()
                        .createAgentWeb()
                        .ready()
                        .go("https://music.163.com/outchain/player?type=4&id=350579058&auto=1&height=90");
            }
        });
        story.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agentWeb1=AgentWeb.with(ThreeFragment.this)
                        .setAgentWebParent(linWeb1,new LinearLayout.LayoutParams(-1,-1))
                        .useDefaultIndicator()
                        .createAgentWeb()
                        .ready()
                        .go("https://music.163.com/outchain/player?type=4&id=526935635&auto=1&height=90");
            }
        });
        return view;
    }



}
