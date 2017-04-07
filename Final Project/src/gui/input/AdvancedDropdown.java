package gui.input;

import java.util.ArrayList;

import datatypes.UDim2;
import gui.Scrollframe;
import gui.events.Event;
import gui.events.Event.EventOperation;
import gui.input.DropdownMenu.Option;
import gui.primitive.Container;
import gui.primitive.Parent;
import gui.primitive.TextButton;

public class AdvancedDropdown extends Container {
	protected TextBox input;
	protected Scrollframe scrollframe;
	
	protected final long TEXT_CHANGE_INTERVAL = 100;
	protected long lastTextChange = -1;
	protected boolean updatedList = false;
	protected boolean scrollframeActive = true;
	
	protected ArrayList<Option> options = new ArrayList<>();
	protected ArrayList<Option> activeOptions = new ArrayList<>();
	
	public final Event Output = new Event ();
	public int test = 0;
	protected class Option {
		public String text;
		public TextButton button;
		public EventOperation operation;
		
		public Option (String text, TextButton button, EventOperation operation) {
			this.text = text;
			this.button = button;
			this.operation = operation;
		}
	}
	
	public AdvancedDropdown (Parent parent, UDim2 position, UDim2 size) {
		super(parent,position,size);
		this.setBackgroundColor(applet.TRANSPARENT);
		this.setBorder(false);
		
		input = new TextBox (this, new UDim2 (), new UDim2 (1, 0, (1f/6f), 0), ".");
		input.setBackgroundColor(applet.FOREGROUND_COLOR);
		input.setTextSize((int) (input.getSize().absoluteY()*0.6f));
		
		input.TextChanged.connect(()->{
			lastTextChange = System.currentTimeMillis();
			updatedList = false;
			toggleScrollframe();
		});
		
		scrollframe = new Scrollframe (this, new UDim2 (0, 0, (1f/6f), 0), new UDim2 (1, 0, (5f/6f), 0));
		scrollframe.changeContentBackground(applet.POPUP_COLOR);
		scrollframe.setZIndex(0);
		scrollframe.getContentHolder().setZIndex(0);
		toggleScrollframe();
		
		input.Output.connect(()->{
			String output = "";
			ArrayList<Option> list = activeOptions.size() != 0 ? activeOptions : options;
			for (int i = 0; i < list.size() && output.equals(""); i++) {
				Option option = list.get(i);
				if (option.button.getText().toLowerCase().contains(input.getText().toLowerCase())) {
					output = option.button.getText();
				}
			}
			
			if (!output.equals("")) {
				input.setText(output);
				this.Output.trigger();
			}
		});
	}
	
	public AdvancedDropdown (Parent parent, UDim2 size) {
		this(parent, new UDim2 (), size);
	}
	
	@Override
	public void update () {
		super.update();
		if (lastTextChange != -1 && !input.getText().equals("") && input.focus)
			if (System.currentTimeMillis() - lastTextChange > TEXT_CHANGE_INTERVAL && !updatedList) {
				updatedList = true;
				
				activeOptions.clear();
				for (Option option: options) {
					if (option.text.toLowerCase().contains(input.getText().toLowerCase())) {
						activeOptions.add(option);
						option.button.setVisible(true);
					} else {
						option.button.setVisible(false);
					}
				}
				toggleScrollframe();
			}
	}
	
	public void toggleScrollframe () {
		if (scrollframeActive != updatedList) {
			updateOptionsHolder();
			UDim2 initialSize = updatedList ? new UDim2 (1, 0, 0, 0) : new UDim2 (1, 0, 5f/6f, 0);
			UDim2 finalSize = updatedList ? new UDim2 (1, 0, 5f/6f, 0) : new UDim2 (1, 0, 0, 0);
			scrollframe.tweenSize(initialSize,  finalSize, 0.1f);
			scrollframeActive = updatedList;
		}
		
		if (!scrollframeActive) {
			for (Option option: options) {
				option.button.setVisible(false);
			}
		}
	}
	
	public void addOption (String... options) {
		for (String option: options) {
			TextButton button = new TextButton (scrollframe.getContentHolder(), new UDim2 (), option);
			button.setDynamicBorder(true);
			button.setBorder(true);
			button.setActiveBorderColor(this.activeBorderColor);
			button.setBackgroundColor(applet.TRANSPARENT);
			button.setBorderColor(this.backgroundColor);
			button.setBorderThickness(this.borderThickness);
			button.setFont(applet.defaultFont);
			button.setTextSize(input.getTextSize());
			button.setVisible(false);
			
			EventOperation operation = () -> {
				input.clearString();
				input.setText(option);
				input.removeFocus();
				this.Output.trigger();
			};
			button.MouseButton1Click.connect(operation);
			this.options.add(new Option(option, button, operation));
		}
		updateOptionsHolder();
	}
	
	public void updateOptionsHolder () {
		float size = (float) this.activeOptions.size();
		scrollframe.setContentSize(size/5f);
		for (int i = 0; i < size; i++) {
			TextButton button = this.activeOptions.get(i).button;
			float buttonSize = 0.2f * (this.size.absoluteY() * (5f/6f));
			button.setSize(new UDim2 (1, -1, 0, buttonSize));
			button.setPosition(new UDim2 (0, 0, 0, i * buttonSize));
		}
	}
	
	public String getOutput () {
		return input.getText();
	}
	
	public String[] getAllOptions () {
		ArrayList<String> allOptions = new ArrayList<>();
		for (Option option: options) {
			allOptions.add(option.text);
		}
		return allOptions.toArray(new String[allOptions.size()]);
	}
}
