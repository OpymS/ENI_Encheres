package fr.eni.tp.encheres.scheduler;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import fr.eni.tp.encheres.bo.Article;
import fr.eni.tp.encheres.bo.ArticleState;
import fr.eni.tp.encheres.dal.ArticleDAO;

@Component
public class ArticleWatcher {

	private ArticleDAO articleDAO;

	public ArticleWatcher(ArticleDAO articleDAO) {
		this.articleDAO = articleDAO;
	}

	//@Scheduled(fixedDelay = 3000)
	public void printArticleCount() {
		int articleCount = articleDAO.countArticles();
		System.err.println("Il y a actuellement "+articleCount+" articles en base de donnée");
	}
	
	
	@Scheduled(cron = "0 * * * * *") //à 0 seconde de chaque minute
	public void updateArticleState() {
		//Récupérer tous les articles dont la date de fin est dépassée
		List<Article> articlesToUpdateToFinished = articleDAO.findArticleToUpdateToFinished();	
		if (articlesToUpdateToFinished.size() != 0) {
			articlesToUpdateToFinished.forEach(article -> {
				article.setState(ArticleState.FINISHED);
				articleDAO.updateArticle(article);
			});
		}
		
		
		//Récupérer aussi tous les articles dont la date d'enchère n'a pas débutée pour update l'état
		List<Article> articlesToUpdateToStarted = articleDAO.findArticleToUpdateToStarted();		
		if (articlesToUpdateToStarted.size() != 0) {
			articlesToUpdateToStarted.forEach(article -> {
				article.setState(ArticleState.STARTED);
				articleDAO.updateArticle(article);
			});
		}
	}

}