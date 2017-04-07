package gui.input;

import java.util.ArrayList;
import java.util.HashMap;

import datatypes.UDim2;
import gui.Scrollframe;
import gui.primitive.Parent;
import gui.primitive.TextButton;

public class InputList extends Scrollframe {
	protected static final int NUMBER_OF_INPUTS_RENDERED = 6;
	
	protected class Input {
		public TextButton button;
		public String input;
		
		public Input (TextButton button, String input) {
			this.button = button;
			this.input = input;
		}
	}
	
	protected HashMap<Input, Boolean> inputs = new HashMap<>();
	protected ArrayList<Input> removalQueue = new ArrayList<>();
	
	public InputList (Parent parent, UDim2 position, UDim2 size) {
		super(parent, position, size);
		updateContainer();
	}
	
	public InputList(Parent parent, UDim2 size) {
		this(parent, new UDim2 (), size);
	}
	
	@Override
	public void update () {
		for (Input input: removalQueue) {
			inputs.remove(input);
			holder.removeChild(input.button);
			updateContainer();
		}
		super.update();
	}
	public void addInput(String input) {
		TextButton button = new TextButton (holder, new UDim2 (), input);
		button.setFont(applet.defaultFont);
		button.setBorder(true);
		button.setBorderColor(applet.TRANSPARENT);
		Input object = new Input (button, input);
		inputs.put(object, true);
		button.MouseButton1Click.connect(()->removalQueue.add(object));
		updateContainer();
	}
	
	public void updateContainer () {
		int numberOfInputs = inputs.size();
		setContentSize((float)numberOfInputs/(float)NUMBER_OF_INPUTS_RENDERED);
		float buttonSize = 1f/numberOfInputs;
		int i = 0;
		for (Input input: inputs.keySet()) {
			input.button.setSize(new UDim2 (1, 0, buttonSize,0));
			input.button.setPosition(new UDim2 (0, 0, buttonSize * i, 0));
			i++;
		}
	}
	
	public String[] getAllInputs() {
		ArrayList<String> allInputs = new ArrayList<>();
		for (Input input: inputs.keySet()) {
			allInputs.add(input.input);
		}
		return allInputs.toArray(new String[allInputs.size()]);
	}
	
	public void clearInputs () {
		inputs = new HashMap<Input, Boolean> ();
		holder.clearAllChildren();
	}
}