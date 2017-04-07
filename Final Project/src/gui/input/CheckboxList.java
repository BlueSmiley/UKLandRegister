package gui.input;

import java.util.ArrayList;
import java.util.HashMap;

import datatypes.UDim2;
import gui.events.Event.EventOperation;
import gui.input.AdvancedDropdown.Option;
import gui.primitive.Container;
import gui.primitive.Parent;
import gui.primitive.TextButton;
import gui.primitive.TextLabel;

public class CheckboxList extends Container {
	protected class Option {
		public String text;
		public Container container;
		public CheckBox box;
		public TextLabel label;
		public Container boxContainer;
		
		public Option (String text, Container container, Container boxContainer, CheckBox box, TextLabel label) {
			this.text = text;
			this.container = container;
			this.boxContainer = boxContainer;
			this.box = box;
			this.label = label;
		}
	}
	
	protected ArrayList<Option> options = new ArrayList<>();
	
	public CheckboxList (Parent parent, UDim2 position, UDim2 size) {
		super(parent, position, size);
		addOption("All");
		CheckBox box = options.get(0).box;
		box.MouseButton1Click.connect(()-> {
			boolean value = box.getValue();
			for (Option option: options) {
				option.box.clicked = value;
			}
		});
	}
	
	public CheckboxList (Parent parent, UDim2 size) {
		this(parent, new UDim2 (), size);
	}
	
	public void addOption (String ... options) {
		for (String option: options) {
			Container optionContainer = new Container (this, new UDim2 (1, 0, 0, 0));
			optionContainer.setBorder(false);
			optionContainer.setBackgroundColor(applet.TRANSPARENT);
			optionContainer.setClipDescendants(true);
			optionContainer.setBackgroundColor(applet.FOREGROUND_COLOR);
			Container boxContainer = new Container (optionContainer, new UDim2 ());
			boxContainer.setBackgroundColor(0xa0ffffff);
			boxContainer.setBorder(true);
			boxContainer.setBorderThickness(2);
			boxContainer.setDynamicBorder(false);
			CheckBox box = new CheckBox (boxContainer, new UDim2 (), new UDim2 (1, 0, 1, 0), "");
			box.setActiveBackgroundColor(0xd0000000);
			TextLabel label = new TextLabel (optionContainer, new UDim2 (), new UDim2 (), option);
			label.setFont(applet.defaultFont);
			label.setTextSize(applet.NORMAL_TEXT_SIZE);
			label.setTextColor(applet.TEXT_COLOR);
			label.setHorizontalAlign(applet.LEFT);
			
			this.options.add(new Option(option, optionContainer, boxContainer, box, label));
		}
		updateOptionsHolder();
	}
	
	public void updateOptionsHolder () {
		float size = 1 / (float) (this.options.size());
		for (int i = 0; i < options.size(); i++) {
			Option option = this.options.get(i);
			option.container.setSize(new UDim2 (1, 0, size, 0));
			option.container.setPosition(new UDim2 (0, 0, size * i, 0));
			option.boxContainer.setSize(new UDim2 (1, 0, 1, 0));
			float squareSize = option.boxContainer.getSize().absoluteY();
			option.boxContainer.setSize(new UDim2 (0, squareSize, 1, 0));
			option.box.setSize(new UDim2 (0.8f, 0, 0.8f, 0));
			option.box.setPosition(new UDim2 (0.1f, 0, 0.1f, 0));
			option.label.setSize(new UDim2 (1, 0, 1, 0));
			option.label.setPosition(new UDim2 (0, squareSize + 5, 0, 0));
		}
	}
	
	public HashMap<String, Boolean> getAllInputs () {
		HashMap<String, Boolean> inputs = new HashMap<> ();
		
		for (Option option: options) {
			if (!(option.label.getText().contains("All"))) {
				String optionText = option.label.getText();
				Boolean value = option.box.getValue();
				inputs.put(optionText, value);
			}
		}
		
		return inputs;
	}
}