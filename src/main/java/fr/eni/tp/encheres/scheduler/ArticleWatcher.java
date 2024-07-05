package fr.eni.tp.encheres.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
	
	//Toute les minutes de la journée (avec cron() !)
	public void updateArticleState() {
		// Récupérer tous les articles dont la date de fin est dépassée;
		
		//Modifier leur état entre les 5 valeurs possibles de ArticleState
		
		
		//Récupérer aussi tous les articles dont la date d'enchère n'a pas débutée pour update si jamais
		
	}

}