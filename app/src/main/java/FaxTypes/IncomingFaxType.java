package FaxTypes;

/**
 * Created by Ismail AY on 31.05.2017.
 */

public class IncomingFaxType {
    int _ID;
    String _Sender;
    String _Receipent;
    String _Date;
    String _Size;
    String _FileUrl;
    String _FilePath;
    int _Seen;
    int _DeleteStatus;


    public IncomingFaxType(){

    }

    public IncomingFaxType(int _ID, String _Sender, String _Receipent, String _Date, String _Size, String _FileUrl, String _FilePath, int _Seen, int _DeleteStatus) {
        this._ID = _ID;
        this._Sender = _Sender;
        this._Receipent = _Receipent;
        this._Date = _Date;
        this._Size = _Size;
        this._FileUrl = _FileUrl;
        this._FilePath = _FilePath;
        this._Seen = _Seen;
        this._DeleteStatus = _DeleteStatus;
    }

    public int getID() {
        return _ID;
    }

    public void setID(int _ID) {
        this._ID = _ID;
    }

    public String getSender() {
        return _Sender;
    }

    public void setSender(String _Sender) {
        this._Sender = _Sender;
    }

    public String getReceipent() {
        return _Receipent;
    }

    public void setReceipent(String _Receipent) {
        this._Receipent = _Receipent;
    }

    public String getDate() {
        return _Date;
    }

    public void setDate(String _Date) {
        this._Date = _Date;
    }

    public String getSize() {
        return _Size;
    }

    public void setSize(String _Size) {
        this._Size = _Size;
    }

    public String getFileUrl() {
        return _FileUrl;
    }

    public void setFileUrl(String _FileUrl) {
        this._FileUrl = _FileUrl;
    }

    public String getFilePath() {
        return _FilePath;
    }

    public void setFilePath(String _FilePath) {
        this._FilePath = _FilePath;
    }

    public int getSeen() {
        return _Seen;
    }

    public void setSeen(int _Seen) {
        this._Seen = _Seen;
    }

    public int getDeleteStatus() {
        return _DeleteStatus;
    }

    public void setDeleteStatus(int _DeleteStatus) {
        this._DeleteStatus = _DeleteStatus;
    }
}
