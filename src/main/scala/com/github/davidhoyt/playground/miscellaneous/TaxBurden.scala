package com.github.davidhoyt.playground.miscellaneous

import scala.annotation.tailrec

object TaxBurden extends App {
  import scala.util._

  val annual_income = 100000.0D //$100,000.00
  val max_contrib_401k = 18000.0D //$18,000.00
  val monthly_mortgage_cost = 7357.0D //$7,357.00
  val monthly_cost_of_living_expenses = 4500.0D //$4,500.00
  val stockAnnualGrowthPercentage = 0.03 //Assume stock grows in worth by 3% annually
  val max_amount_of_stock = 100000.0D

  case class Plan(stockAmount: Double)

  case class Bracket(range: (Double, Double), base: Double, multiplier: Double, over: Double)
  object Bracket {
    //http://www.forbes.com/sites/kellyphillipserb/2015/05/14/2015-tax-rates-standard-deductions-personal-exemptions-credit-amounts-more/
    val values = Seq(
      Bracket((    0.00,         18450.00),      0.00,  0.10,      0.00),
      Bracket((18450.00,         74900.00),   1845.00,  0.15,  18450.00),
      Bracket((74900.00,        151200.00),  10312.50,  0.25,  74900.00),
      Bracket((151200.00,       230450.00),  29387.50,  0.28, 151200.00),
      Bracket((230450.00,       411500.00),  51577.50,  0.33, 230450.00),
      Bracket((411500.00,       464850.00), 111324.50,  0.35, 411550.00),
      Bracket((464850.00, Double.MaxValue), 129996.50, 0.396, 464850.00)
    )

    val lowest = values.head
    val highest = values.last

    def given(income: Double): Bracket =
      values.collectFirst {
        case b @ Bracket((lower, upper), _, _, _)  if lower < income && income <= upper => b
      }.getOrElse(???)
  }

  val annualMortgageExpense = monthly_mortgage_cost * 12.0
  val annualCostOfLivingExpense = monthly_cost_of_living_expenses * 12.0

  val max_adjusted_no_stock = annual_income - annualMortgageExpense - annualCostOfLivingExpense
  val max_adjusted_all_stock = annual_income + (annual_income * stockAnnualGrowthPercentage)

  def calculateScore(plan: Plan): Double = {
    val Plan(stockAmount) = plan

    //Do not factor in costs that wouldn't adjust my tax bracket.
    val taxableIncome = annual_income - stockAmount - max_contrib_401k
    val bracket @ Bracket((lower, upper), base, multiplier, over) = Bracket.given(taxableIncome)

    val charitableContributions = taxableIncome * 0.10

    val taxBurden = (taxableIncome - over) * multiplier + base

    val stockGrowth = stockAmount * stockAnnualGrowthPercentage

    val adjustedIncomeAfterAddlExpenses = taxableIncome - annualCostOfLivingExpense - annualMortgageExpense - charitableContributions - taxBurden


    //Assign a weight to stock vs take home pay that reflects its relative importance:
    val score = (stockGrowth / max_adjusted_all_stock) * 0.5 * (adjustedIncomeAfterAddlExpenses / max_adjusted_no_stock) * 0.5

    println(s"stockAmount: $stockAmount, score: $score, adjustedIncome: $taxableIncome, adjustedIncomeAfterAddlExpenses: $adjustedIncomeAfterAddlExpenses, annualMortgageExpense $annualMortgageExpense, annualCostOfLivingExpense: $annualCostOfLivingExpense, charitableContributions: $charitableContributions, taxBurden: $taxBurden, stockGrowth: $stockGrowth, bracket: $bracket")

    score
  }

  def minorPerturbation(plan: Plan): Plan = {
    //Randomly increases or decreases the stock amount by up to +/- $500
    val Plan(stockAmount) = plan
    val upOrDown = if (Random.nextBoolean()) 1.0D else -1.0D

    val newStockAmount = upOrDown * ((Random.nextDouble() * 5.0) * Random.nextDouble() * 100.0)
    Plan(math.max(0, math.min(max_amount_of_stock, newStockAmount)))
  }

  def majorPerturbation(plan: Plan): Plan = {
    val newStockAmount = Random.nextDouble() * max_amount_of_stock
    //val upOrDown = if (Random.nextBoolean()) 1.0D else -1.0D
    //val newStockAmount = math.max(0.0, math.min(max_amount_of_stock, plan.stockAmount + (upOrDown * plan.stockAmount * (Random.nextDouble() * 100.0 % 75.0) / 100.0)))
    Plan(newStockAmount)
  }


  var current_plan = Plan(stockAmount = 0.0)
  val initial_score = calculateScore(current_plan)

  var temperature = 1.0D
  var best_plan_found = current_plan
  var global_score, local_score = initial_score

  println(s"Starting with a current score of: $initial_score")

  while (temperature > 0.0001D) {
    local_score = calculateScore(current_plan)

    if (local_score > 0.0 && local_score > global_score) {
      global_score = local_score
      best_plan_found = current_plan
    }

    if (Random.nextDouble() > temperature)
      current_plan = majorPerturbation(current_plan)
    else
      current_plan = minorPerturbation(current_plan)

    temperature = temperature * 0.9999
  }

  val Plan(bestStockAmount) = best_plan_found
  val taxBurden = calculateScore(best_plan_found)

  println(s"You should allocate $$$bestStockAmount towards stock")

//  val (initialTemperature, coolingRate) = (100000.0, 0.0003)
//
//  // If the new solution is better, accept it, else compute acceptance probability
//  def acceptanceProbability(energies:(Double,Double), temperature:Double) = {
//    val diff = energies._1 - energies._2
//    if (diff < 0 ) 1.0 else math.exp(diff/temperature)
//  }
//
//  @tailrec def compute( best:Plan, temp:Double):Plan = {
//    if (temp > 1) {
//      val newSolution = majorPerturbation( best )
//      val currentEnergy = calculateScore(best)
//      val neighbourEnergy = calculateScore(newSolution)
//
//      // Decide if we should accept the neighbour
//      val accept = (acceptanceProbability((currentEnergy, neighbourEnergy), temp) > math.random) &&
//        (calculateScore(newSolution) > calculateScore(best))
//      compute( if (accept) {
//        printf("\ncalculateScore: %.2f, Temperature: %.2f", calculateScore(newSolution), temp)
//        newSolution
//      } else best, (1-coolingRate)*temp)
//    } else best
//  }
//
//  println(compute(Plan(stockAmount = 0.0), initialTemperature))

  //double check tax burden when zero stock -- should be > $100,000. Consider costs for benefits...
}
