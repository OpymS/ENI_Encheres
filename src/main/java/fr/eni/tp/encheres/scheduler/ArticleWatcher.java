package fr.eni.tp.encheres.scheduler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import fr.eni.tp.encheres.bo.Article;
import fr.eni.tp.encheres.bo.ArticleState;
import fr.eni.tp.encheres.dal.ArticleDAO;
import fr.eni.tp.encheres.dal.UserDAO;

@Component
public class ArticleWatcher {
	private static final Logger articleWatcherLogger = LoggerFactory.getLogger(ArticleWatcher.class);

	private ArticleDAO articleDAO;
	private UserDAO userDAO;

	public ArticleWatcher(ArticleDAO articleDAO, UserDAO userDAO) {
		this.articleDAO = articleDAO;
		this.userDAO = userDAO;
	}

	//@Scheduled(fixedDelay = 3000)
	public void printArticleCount() {
		int articleCount = articleDAO.countArticles();
		articleWatcherLogger.info("Il y a actuellement "+articleCount+" articles en base de donnée");
	}
	
	
	@Scheduled(cron = "0 * * * * *") //à 0 seconde de chaque minute
	public void updateArticleState() {
		//Récupérer tous les articles dont la date de fin est dépassée
		List<Article> articlesToUpdateToFinished = articleDAO.findArticleToUpdateToFinished();	
		if (articlesToUpdateToFinished.size() != 0) {
			articlesToUpdateToFinished.forEach(article -> {
				// On ajoute aux crédits du vendeur le montant d'achat de l'article avant de la passé en FINISHED
				// Et on modifie en BDD.
				
				articleWatcherLogger.info("prix article : "+ article.getCurrentPrice() + " vendeur/credit : "+article.getSeller().getPseudo()+"/"+article.getSeller().getCredit());
				article.getSeller().setCredit(article.getSeller().getCredit() + article.getCurrentPrice());
				userDAO.updateCredit(article.getSeller());
				
				
				// On modifie l'état
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