package com.cryptoregistry.mockumatrix;

public class QuoteRunner {

	public static void main(String[] args) {
		
	//	String srcImgFolder = "src/main/resources/quote-source/jim";
	//	String srcImgFolder = "src/main/resources/quote-source/jimcrow";
		String srcImgFolder = "C:/Users/Dave/Desktop/blog/saruman";
		
	//	String [] quoteStrings = {
	//			"That so many have justified removing the [Confederate battle] flag on the basis of the shooting demonstrates the power of symbolism and narrative over history and fact.",
	//			"We must recognize the immense moral thrust that drives political correctness, globalism, egalitarianism, and all the rest [against us].",
	//			"...at the moment, any kind of extra-legal or violent action�no matter how brilliantly conceived�would bring to Identitarianism the shame and horror that will forever accompany the name Dylann Roof.",
	//			"That�s not to say that many of us don�t dream of The Day, when everything will change.",
	//			"Excerpts from Richard B. Spencer, writing about his acolyte Dylann Roof, on www.radixjournal.com, dated June 23, 2015"
	//	};
		
	//	String [] quoteStrings = {
	//				"Do you know the origin of all those twitter users?",
	//				"They were once frustrated bloggers...with thoughts and dreams and stories all their own...",
	//				"Truncated by the dark powers, twisted into a cramped, noisome 140 characters of chatter...",
	//				"Addicted to emoji and fake news...a rotten and ruined race...",
	//				"But now, perfected in my fighting mockumatrix!"
	//		};
		
	//	String [] quoteStrings = {
	//			"Dear Twitter:\n Your need for #selfies is not the same thing as my need for political empowerment.",
	//			"Dear Twitter:\n Your #bigscore is not the same thing as my hard-earned dollar.",
	//			"Dear Twitter:\n Your #taxincentive is not the same thing as my earned income credit.",
	//			"Dear Twitter:\n Your #repression is not the same thing as my self-discipline.",
	//			"Dear Twitter:\n Your #whitegenocide is not the same thing as my diversity.",
	//			"Dear Twitter:\n Your #blanketignorance is not the same thing as my studied indifference.",
	//			"Dear Twitter:\n Your #penchantformalice is not the same thing as my outrage.",
	//			"Dear Twitter:\n Your #ADHD is not the same thing as my kid's rampant creativity.",
	//			"Dear Twitter:\n Your canned #tweet is not the same thing as my fake status.",
	//			"Dear Twitter:\n Your #emoji is not my smiley face or my peace sign.",
	//			"Dear Twitter:\n My natural euphoria is not the same thing as your #ecstasy coma.",
	//			"Dear Twitter:\n Your #instantboredom is not the same thing as my sudden clarity."
	//	};
		
	String [] quoteStrings = {
			"Given that all fiction is escapist...",
			"Was having a Black President so shocking?",
			"Is the reality of other religions really so hard to accept?",
			"Escapism can be manipulated and used to convince people to live in a fantasy.",
			"This can be harmless, a good night out...",
			"Or it can become a nightmare."
	};
		
		
		
		int size = 32;
				
		
		QuoteGen generator = new QuoteGen(srcImgFolder,quoteStrings, size);
		generator.gen();

	}

}
