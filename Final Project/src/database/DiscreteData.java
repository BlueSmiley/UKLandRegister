package database;

public class DiscreteData
{
    public int[] data;
    public String[] labels;
    public String title;

    public DiscreteData(int[] data, String[] labels, String title)
    {
        this.data = data;
        this.labels = labels;
        this.title = title;
    }

    public int[] getData() {return data;}

    public String[] getLabels() {return labels;}

    public String getTitle() {return title;}
}
