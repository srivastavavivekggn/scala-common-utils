# Rule Processing

Rule processing is based on parsing and evaluating text-based rules.

This implementation of rule processing is based on [FastParse](http://www.lihaoyi.com/fastparse), which
handles the string parsing and evaluation at the same time.

In this document, you will see references to `P[_]`, which is FastParse's _Parser_ type.
Parsers can be transformed via `.map` functions, just like Scala collections.  The type essentially defines
what is being captured (similar to regex group) -- a `P[Unit]` means you aren't capturing anything.

Parsers can be combined to form more complex processing and capture logic.

Here is a basic example of a parser, a mapping fn, and parsing a string into a result:
```scala

  // the value we'll be testing
  val stringToTest = "Say hello to everyone!"

  // a parser which will match and capture the string "hello"
  def helloParser[_:P]: P[String] = P("hello").!

  // a parser based on the above string parser, but which will caputure a boolean instead
  def isHelloParser[_: P]: P[Boolean] = helloParser.map(isHello)

  // our mapping function, converts the captured string into a boolean
  def isHello(capturedStringVal: String): Boolean = {
    capturedStringVal.equals("hello")
  }

  // parse the test string using our string parser
  helloParser.parse(stringToTest) match {
    case Parsed.Success(value, idx) => // value is the string "hello"
    case _ => // ...
  }

  // parse the test string using our boolean parser
  isHelloParser.parse(stringToTest) match {
    case Parsed.Success(value, idx) => // value is a boolean true
    case _ => // ...
  }
```

### GOAL
The goal of this project is to be able to utilize simple text-based rules to manage
complex business requirements that otherwise would have to live in the code.  With the
large number and increasing complexity of Incentive programs, coding for every scenario is
nearly impossible.

Ultimately, we'd like to have something like this: (event eligibility)
```
(AGE >= 55 AND GENDER = "F")
  OR
   (AGE < 55 AND GENDER = "F" AND CONDITIONS hasOneOf \["SNOMED:xyz"\])
```

Or even eventually: (program eligibility)
```
 RELATIONSHIP is "Primary" AND ZIPCODE is within 50 MILES of 07030
```


### Usages
If this works as expected, it could be used for:
 - Event eligibility (Age, Gender, Health Conditions, etc.)
 - Program eligibility (especially consumer programs)
 - Logic driven display text
 - Complex event processing or reward rules (e.g., tiered rewards, etc.)


### Definition
In general terms, a rule is a parser `P[T]` that can be mapped to a `P[Boolean]` using a given context.

Most of our rules follow one of two patterns:

`P[(String, String)]` - captures _Field_ and _Operator_

e.g., `LAST_NAME exists`, `ZIP_CODE is missing`

OR

`P[(String, String, T)]` - captures _Field_, _Operator_, and comparison _Value_

The `T` here represents some type, like an `Int`, `String`, `Seq[Int]`, etc.
e.g., `AGE >= 18`, `GENDER is F`


### Processing

We'll use `AGE >= 18` as our example rule here:

The rule is:
1. Parsed from a String into a `P[X]`  (`X` represents a tuple of `(String, String, Int)` - 'AGE', '>=', and 18)
2. Mapped from `P[X]` to a `P[Boolean]` using a mapper function and an evaluation context (more on this below)
3. Evaluated as true/false (possibly with other rules as a part of a RuleSet)

Utilizing mapping functions we can easily transform values to operate on, like number/string comparisons, lists, etc.


#### String Rules

**String processing is CASE INSENSITIVE **


| Operator | Description |
| :--: | :---: |
| is | _Field_ value is equal to _Value_ |
| is not | _Field_ value is NOT equal to _Value_ |
| contains | _Field_ contains _Value_ |
| starts with | _Field_ starts with _Value_ |
| ends with | _Field_ ends with _Value_ |

*Examples*

```
NAME starts with "Bo"

STATE is NY

GENDER is not F
```

#### String Array Rules

** String processing is CASE INSENSITIVE **

Used for a single value on the left, with an array of values on the right.

| Operator | Description |
| :--: | :---: |
| found in | _Field_ value is present in _Value_ |
| not found in | _Field_ value is NOT present in _Value_ |

*Examples*

```
NAME is in ["James", "Bob", "Tom"]

STATE is not in ["CA", "MS", "TX"]
```

#### Array of String Rules

** String processing is CASE INSENSITIVE **

Used for an array of values on the left, with an array of values on the right.

| Operator | Description |
| :--: | :---: |
| has one of | _Field_ array contains at least one from _Value_ |
| has all of | _Field_ array contains all of _Value_ |

*Examples*

```
NAMES has one of ["James", "Bob", "Tom"] 
STATES has all of ["CA", "MS", "TX"]
```
(where `NAMES = ["Tim", "Sue", "Tom"]` and `STATES = ["CA", "TX"]`)

#### Number Rules

| Operator | Description |
| :--: | :---: |
| <  | _Field_ is less than _Value_ |
| <= | _Field_ is less than or equal to _Value_ |
| =  | _Field_ is equal to _Value_ |
| != | _Field_ is NOT equal to _Value_ |
| >  | _Field_ is greater than _Value_ |
| >= | _Field_ is greater than or equal to _Value_ |

*Examples*
```
AGE >= 18
EYES = 2
HAIR != 0
```

#### Number Array Rules

Just like String Array rules, you can match against an array of numeric values.

| Operator | Description |
| :--: | :---: |
| found in | _Field_ value is present in _Value_ |
| not found in | _Field_ value is NOT present in _Value_ |

*Examples*

```
YEAR is in [2018, 2019, 2020]
```

#### Existence Rules

| Operator | Description |
| :--: | :---: |
| exists  | _Field_ is found in the context and has a non-null/non-None value |
| does not exist | _Field_ is missing from the context or has a null/None value|

*Examples*
```
AGE is present
HAIR is missing
```

#### Temporal Rules

Temporal rules are used to match/compare on dates and times, including date math and keywords like 'now', 'tomorrow', etc.

The parser rules use the same operators as a Numeric Rule:

| Operator | Description |
| :--: | :---: |
| <  | **DEPRECATED** _Field_ is less than _Value_ |
| before  | _Field_ is less than _Value_ |
| <= | **DEPRECATED** _Field_ is less than or equal to _Value_ |
| before or equal to | _Field_ is less than or equal to _Value_ |
| =  | **DEPRECATED** _Field_ is equal to _Value_ |
| equal to  | _Field_ is equal to _Value_ |
| != | **DEPRECATED** _Field_ is NOT equal to _Value_ |
| not equal to | _Field_ is NOT equal to _Value_ |
| >  | **DEPRECATED** _Field_ is greater than _Value_ |
| after  | _Field_ is greater than _Value_ |
| >= | **DEPRECATED** _Field_ is greater than or equal to _Value_ |
| after or equal to | _Field_ is greater than or equal to _Value_ |

*Examples*
```
START_DATE after today

START_DATE before or equal to 2020-02-15T00:00:00

START_DATE > today + 7 days
```

## Rule Sets
A rule set is simply a joining of rules using AND/OR syntax, and grouping rules within parenthesis.

*Examples*
```
(AGE >= 18 AND EYES = 2) OR HAIR != 0

NAME startsWith "Bo" AND STATE is "NY" AND GENDER is not "F"
```

The processing here is simple, each clause gets mapped to a `P[Boolean]`,
and then the boolean values are evaluated with the proper AND/OR join.

```
(AGE >= 18 AND EYES = 2) OR HAIR != 0

becomes

(bool && bool) || bool
```



## Evaluation Context
In order to map a `P[X]` into a `P[Boolean]` we need some kind of context with a specific user's data.

In general rules processing terms, when we first parse our rule:

 _Field_ _Operator_ _Value_

we have a tuple of strings `(String, String, String)`.  The first string in our tuple is
the data field we're operating on, so we need to ask our evaluation context
for the specific value for this user.

For the rule:
`AGE >= 18`

We ask the evaluation context if it knows about a field called `AGE`, and if so,
we retrieve the value which we can then operate on.
