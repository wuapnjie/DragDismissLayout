package com.xiaopo.flying.dragdismisslayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.text_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new TextAdapter());

        DragDismissLayout dragDismissLayout = (DragDismissLayout) findViewById(R.id.drag_dismiss_layout);

        dragDismissLayout.setDragCallback(new DragDismissLayout.DragCallback() {
            @Override
            public void onDrag(int offset) {
                Log.d("Drag", "onDrag: offset->" + offset);
            }

            @Override
            public void onReadyDismiss() {
                Log.d("Drag", "onReadyDismiss: ");
            }

            @Override
            public void onDismiss(int direction) {
                if (direction == DragDismissLayout.DIRECTION_UP) {
                    Log.d("Drag", "onDismiss: UP");
                } else {
                    Log.d("Drag", "onDismiss: DOWN");
                }
            }

            @Override
            public void onDragCanceled() {
                Log.d("Drag", "onDragCanceled: ");
            }
        });
    }

}
