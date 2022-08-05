package ru.smartro.worknote.abs

/**
alertDialog.show();
WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
lp.copyFrom(alertDialog.getWindow().getAttributes());
lp.width = 150;
lp.height = 500;
lp.x=-170;
lp.y=100;
alertDialog.getWindow().setAttributes(lp);

AlertDialog.Builder builder = new AlertDialog.Builder(this);
builder.setView(layout);
builder.setTitle("Title");
alertDialog = builder.create();
alertDialog.show();
alertDialog.getWindow().setLayout(600, 400); //Controlling width and height.
 */
class TodoView {
}