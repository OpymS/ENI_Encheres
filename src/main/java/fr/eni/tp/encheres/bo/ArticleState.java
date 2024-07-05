package fr.eni.tp.encheres.bo;

public enum ArticleState {
	CANCELED,
	NOT_STARTED,
	STARTED,
	FINISHED,
	RETRIEVED;
	
	public static ArticleState toArticleState(int entryInt) {
		ArticleState articleState = null;
		switch(entryInt) {
		case 0:
			articleState = ArticleState.CANCELED;
			break;
		case 1:
			articleState = ArticleState.NOT_STARTED;
			break;
		case 2:
			articleState = ArticleState.STARTED;
			break;
		case 3:
			articleState = ArticleState.FINISHED;
			break;
		case 4:
			articleState = ArticleState.RETRIEVED;
			break;
		}
		return articleState;
	}
	
	public static int toInt(ArticleState articleState) {
		int result = 0;
		switch(articleState) {
		case CANCELED:
			result = 0;
			break;
		case NOT_STARTED:
			result = 1;
			break;
		case STARTED:
			result = 2;
			break;
		case FINISHED:
			result = 3;
			break;
		case RETRIEVED:
			result = 4;
			break;
		}
		return result;
	}
	
}
