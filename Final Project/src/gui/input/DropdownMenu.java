package gui.input;

import java.util.ArrayList;
import datatypes.UDim2;
import gui.events.Event.EventOperation;
import gui.primitive.Container;
import gui.primitive.Parent;
import gui.primitive.TextButton;

public class DropdownMenu extends TextButton implements Input {

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
	
	protected boolean active = false;
	protected Container clipper;
	protected Container optionsHolder;
		
	protected ArrayList<Option> options = new ArrayList<> ();
	
	public DropdownMenu (Parent parent, UDim2 position, UDim2 size, String text) {
		super(parent, position, size, text);
		clipper = new Container (this, new UDim2 (0, 0, 1, 1), new UDim2 (1, 2, 0, 0));
		clipper.setBorder(false);
		clipper.setBackgroundColor(0x00ffffff);
		clipper.setClipDescendants(true);
		optionsHolder = new Container (clipper, new UDim2(1,0,1,0));
		optionsHolder.setBackgroundColor(0x00ffffff);
		optionsHolder.setBorder(false);
	
		this.MouseButton1Click.connect(() -> {
			if (!tweening) {
				active = !active;
				if (active) {
					clipper.tweenSize(new UDim2 (1, 0, options.size(), 0),0.2f);
				} else {
					clipper.tweenSize(new UDim2(1,0,0,0), 0.2f);
				}
			}
		});
	}
	
	@Override
	public void update () {
		super.update();
		
	}
	
	public DropdownMenu(Parent parent, UDim2 size, String text) {
		this(parent, new UDim2 (), size, text);
	}
	
	public void addOption (String... options) {
		for (String option: options) {
			TextButton button = new TextButton (optionsHolder, new UDim2 (), option);
			button.setDynamicBorder(true);
			button.setBorder(true);
			button.setActiveBorderColor(this.activeBorderColor);
			button.setBackgroundColor(this.backgroundColor);
			button.setBorderColor(this.backgroundColor);
			button.setBorderThickness(this.borderThickness);
			button.setFont(this.font);
			
			EventOperation operation = () -> setText(option);
			button.MouseButton1Click.connect(operation);
			this.options.add(new Option(option, button, operation));
		}
		updateOptionsHolder();
	}
	
	public void updateOptionsHolder () {
		float size = (float) this.options.size();
		for (int i = 0; i < size; i++) {
			TextButton button = this.options.get(i).button;
			button.setSize(new UDim2 (1, 0, 1f/size, 0));
			button.setPosition(new UDim2 (0, 0, ((float) i)/size, 0));
		}
	}
	
	@Override
	public String getValue () {
		return this.text;
	}
}
