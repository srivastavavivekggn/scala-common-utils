package com.srivastavavivekggn.scala.util.test.generator

import com.srivastavavivekggn.scala.util.random.RandomUtils

/**
  * Utility to randomly generate names
  */
object NameGenerator {

  final val FIRST_NAME = "firstName"
  final val FIRST_NAME_FEMALE = "femaleFirstName"
  final val FIRST_NAME_MALE = "femaleFirstName"
  final val LAST_NAME = "lastName"
  final val ANIMAL = "animal"
  final val ADJECTIVE = "adjective"
  final val VERB_ING = "ingVerb"
  final val ADJECTIVE_OR_VERB = "adjectiveOrVerb"

  /**
    * Set of female first names
    */
  final val femaleFirstNames: Seq[String] = Seq(
    "Aaliyah", "Abigail", "Addison", "Alexa", "Alexandra", "Alexis", "Allison",
    "Alyssa", "Amelia", "Anna", "Annabelle", "Aria", "Ariana", "Arianna", "Ashley", "Aubree",
    "Aubrey", "Audrey", "Autumn", "Ava", "Avery", "Bella", "Brianna", "Brooklyn", "Camila",
    "Caroline", "Charlotte", "Chloe", "Claire", "Elizabeth", "Ella", "Ellie", "Emily", "Emma",
    "Eva", "Evelyn", "Faith", "Gabriella", "Genesis", "Gianna", "Grace", "Hailey", "Hannah",
    "Harper", "Isabella", "Jocelyn", "Julia", "Katherine", "Kayla", "Kaylee", "Kennedy", "Khloe",
    "Kylie", "Lauren", "Layla", "Leah", "Lillian", "Lily", "London", "Lucy", "Lydia", "Mackenzie",
    "Madeline", "Madelyn", "Madison", "Makayla", "Maya", "Melanie", "Mia", "Mila", "Morgan", "Naomi",
    "Natalie", "Nevaeh", "Nicole", "Nora", "Olivia", "Paisley", "Penelope", "Peyton", "Piper",
    "Riley", "Ruby", "Sadie", "Samantha", "Sarah", "Savannah", "Scarlett", "Serenity", "Skylar",
    "Sofia", "Sophia", "Sophie", "Stella", "Sydney", "Taylor", "Victoria", "Violet", "Zoe", "Zoey"
  )

  /**
    * Set of male first names
    */
  final val maleFirstNames: Seq[String] = Seq(
    "Aaron", "Adam", "Adrian", "Aiden", "Alexander", "Andrew", "Angel", "Anthony", "Austin", "Ayden",
    "Benjamin", "Bentley", "Blake", "Brandon", "Brayden", "Brody",
    "Caleb", "Camden", "Cameron", "Carson", "Carter", "Charles", "Chase", "Christian", "Christopher",
    "Colton", "Connor", "Cooper", "Damian", "Daniel", "David", "Dominic", "Dylan",
    "Easton", "Eli", "Elijah", "Ethan", "Evan", "Gabriel", "Grayson", "Henry", "Hudson", "Hunter",
    "Ian", "Isaac", "Isaiah", "Jace", "Jack", "Jackson", "Jacob", "James", "Jase", "Jason",
    "Jaxon", "Jaxson", "Jayden", "Jeremiah", "John", "Jonathan", "Jordan", "Jose", "Joseph",
    "Joshua", "Josiah", "Juan", "Julian", "Justin", "Kayden", "Kevin", "Landon", "Levi", "Liam",
    "Lincoln", "Logan", "Lucas", "Luis", "Luke", "Mason", "Matthew", "Michael", "Nathan",
    "Nathaniel", "Nicholas", "Noah", "Nolan", "Oliver", "Owen", "Parker", "Robert", "Ryan",
    "Samuel", "Sebastian", "Thomas", "Tristan", "Tyler", "William", "Wyatt", "Xavier", "Zachary")

  /**
    * Set of all first names combined
    */
  final val firstNames: Seq[String] = femaleFirstNames ++ maleFirstNames

  /**
    * Set of last names
    */
  final val lastNames: Seq[String] = Seq(
    "Adams", "Allen", "Anderson", "Bailey", "Baker", "Barnes", "Bell",
    "Bennett", "Brooks", "Brown", "Butler", "Campbell", "Carter", "Clark", "Collins", "Cook", "Cooper",
    "Cox", "Cruz", "Davis", "Diaz", "Edwards", "Evans", "Fisher", "Flores", "Foster", "Garcia", "Gomez",
    "Gonzalez", "Gray", "Green", "Gutierrez", "Hall", "Harris", "Hernandez", "Hill", "Howard", "Hughes",
    "Jackson", "James", "Jenkins", "Johnson", "Jones", "Kelly", "King", "Lee", "Lewis", "Long", "Lopez",
    "Martin", "Martinez", "Miller", "Mitchell", "Moore", "Morales", "Morgan", "Morris", "Murphy", "Myers",
    "Nelson", "Nguyen", "Ortiz", "Parker", "Perez", "Perry", "Peterson", "Phillips", "Powell", "Price",
    "Ramirez", "Reed", "Reyes", "Richardson", "Rivera", "Roberts", "Robinson", "Rodriguez", "Rogers", "Ross",
    "Russell", "Sanchez", "Sanders", "Scott", "Smith", "Stewart", "Sullivan", "Taylor", "Thomas", "Thompson",
    "Torres", "Turner", "Walker", "Ward", "Watson", "White", "Williams", "Wilson", "Wood", "Wright", "Young"
  )

  /**
    * Set of animal names. Can all be made plural by adding an 's', which is why
    * things like 'Wolf' are not included
    */
  final val animalNames: Seq[String] = Seq(
    "Aardvark", "Alligator", "Alpaca", "Anaconda", "Antelope", "Armadillo",
    "Badger", "Bat", "Bear", "Beetle", "Bobcat", "Buffalo",
    "Cat", "Cheetah", "Chicken", "Chimpanzee", "Chipmunk", "Cobra", "Condor", "Coyote", "Crocodile", "Crow",
    "Dinosaur", "Dog", "Dolphin", "Duck", "Dragon",
    "Eagle", "Eel", "Emu",
    "Falcon", "Ferret", "Flamingo", "Frog",
    "Goat", "Gopher", "Grasshopper",
    "Hamster", "Hare", "Hawk", "Hornet", "Hammerhead",
    "Iguana", "Impala",
    "Kangaroo",
    "Ladybug", "Leopard", "Lion", "Lizard", "Llama", "Lobster",
    "Mustang", "Marlin",
    "Orca", "Otter", "Owl",
    "Panda", "Panther", "Parrot", "Pelican", "Penguin",
    "Rabbit", "Rattlesnake", "Raven", "Rooster", "Raptor",
    "Snake", "Spider", "Shark",
    "Tiger", "Tomcat",
    "Wasp",
    "Zebra"
  )

  /**
    * Set of adjectives
    */
  final val adjectives: Seq[String] = Seq(
    "Red", "Orange", "Yellow", "Green", "Blue", "Purple", "Pink", "Magenta",
    "Black", "Brown", "White", "Grey", "Golden", "Silver", "Platinum",
    "Angry", "Athletic", "Active", "Amazing", "Astonishing", "Astounding", "Awesome",
    "Cursed",
    "Dominant",
    "Elite", "Extraordinary",
    "Fearsome", "Fierce", "Ferocious", "Forceful", "Furious", "Frantic", "Fabulous",
    "Glorious",
    "Happy",
    "Incredible",
    "Jubilant",
    "Mad", "Marvelous", "Mighty",
    "Noble",
    "Powerful", "Phenomenal",
    "Relentless", "Resolute", "Resilient",
    "Savage", "Stout", "Steadfast",
    "Tenacious", "Tough", "Toxic",
    "Unbeatable", "Unstoppable", "Unyielding",
    "Victorious",
    "Wild"
  )

  /**
    * Set of verbs ending with ING
    */
  final val ingVerbs: Seq[String] = Seq(
    "Annihilating",
    "Charging", "Conquering", "Crouching", "Crushing",
    "Demolishing", "Dominating",
    "Fighting", "Flying",
    "Jumping",
    "Laughing", "Leaping",
    "Raiding", "Riding", "Roaring", "Running",
    "Soaring",
    "Thrashing",
    "Winning"
  )

  /**
    * Combination of adjectives and verbs for convenience
    */
  final val adjectivesAndVerbs = adjectives ++ ingVerbs

  /**
    * Map keys to appropriate lists of data
    */
  private final val keyMapping = Map(
    FIRST_NAME -> firstNames,
    FIRST_NAME_FEMALE -> femaleFirstNames,
    FIRST_NAME_MALE -> maleFirstNames,
    LAST_NAME -> lastNames,
    ANIMAL -> animalNames,
    ADJECTIVE -> adjectives,
    VERB_ING -> ingVerbs,
    ADJECTIVE_OR_VERB -> adjectivesAndVerbs
  )

  /**
    * Convenience wrapper for RandomUtils method
    *
    * @param lst the list
    * @return the random element from the list
    */
  private def getRandom(lst: Seq[String]): String = RandomUtils.getRandomItem(lst)

  /**
    * Get a random first name
    *
    * @return a random first name
    */
  def getFirstName: String = getRandom(firstNames)

  /**
    * Get a random female first name
    *
    * @return the female first name
    */
  def getFemaleFirstName: String = getRandom(femaleFirstNames)

  /**
    * get a random male first name
    *
    * @return a random male first name
    */
  def getMaleFirstName: String = getRandom(maleFirstNames)

  /**
    * Get a random last name
    *
    * @return the random last name
    */
  def getLastName: String = getRandom(lastNames)

  /**
    * Get a first + last name randomly
    *
    * @return the first and last name as a tuple
    */
  def getName: (String, String) = (getRandom(firstNames), getRandom(lastNames))

  /**
    * Get a female first + last name randomly
    *
    * @return the first and last name as a tuple
    */
  def getFemaleName: (String, String) = (getRandom(femaleFirstNames), getRandom(lastNames))

  /**
    * Get a male first + last name randomly
    *
    * @return the first and last name as a tuple
    */
  def getMaleName: (String, String) = (getRandom(maleFirstNames), getRandom(lastNames))

  /**
    * Fill in placeholders using random values
    *
    * @param format the string format. e.g., "My name is %s %s"
    * @param keys   the data keys to use "firstName", "lastName", etc.
    * @return the formatted string with replaced placeholders
    */
  def get(format: String, keys: String*): String = {
    format.format(
      keys.map(k => keyMapping.getOrElse(k, Seq(""))).map(getRandom): _*
    )
  }
}
