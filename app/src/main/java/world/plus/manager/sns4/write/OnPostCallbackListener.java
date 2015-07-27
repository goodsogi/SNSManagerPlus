package world.plus.manager.sns4.write;

public interface OnPostCallbackListener {
	public void onCompleted(int snsName);
	public void onError(int snsName);

}
