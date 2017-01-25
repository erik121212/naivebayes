package training.erik121212.nl.naivebayes

import java.io.ByteArrayInputStream
import java.util

import org.dmg.pmml.{DataField, FieldName, OpType, PMML}
import org.jpmml.evaluator._
import org.jpmml.model.{ImportFilter, JAXBUtil}
import org.xml.sax.InputSource

object Main {

  def main(args: Array[String]): Unit = {
    example1()
  }


  def example1() {
    var example =
      """
      <PMML version="4.2" xmlns="http://www.dmg.org/PMML-4_2">
        <Header copyright="M04D140">
          <Application name="KNIME" version="3.2.0"/>
        </Header>
        <DataDictionary numberOfFields="5">
          <DataField name="Outlook" optype="categorical" dataType="string">
            <Value value="Rainy"/>
            <Value value="Overcast"/>
            <Value value="Sunny"/>
          </DataField>
          <DataField name="Temp" optype="categorical" dataType="string">
            <Value value="Hot"/>
            <Value value="Mild"/>
            <Value value="Cool"/>
          </DataField>
          <DataField name="Humidity" optype="categorical" dataType="string">
            <Value value="High"/>
            <Value value="Normal"/>
          </DataField>
          <DataField name="Windy" optype="categorical" dataType="string">
            <Value value="false"/>
            <Value value="true"/>
          </DataField>
          <DataField name="Play Golf" optype="categorical" dataType="string">
            <Value value="No"/>
            <Value value="Yes"/>
          </DataField>
        </DataDictionary>
        <NaiveBayesModel isScorable="true" modelName="KNIME PMML Naive Bayes model" threshold="0.0" functionName="classification">
          <MiningSchema>
            <MiningField name="Outlook" invalidValueTreatment="asIs"/>
            <MiningField name="Temp" invalidValueTreatment="asIs"/>
            <MiningField name="Humidity" invalidValueTreatment="asIs"/>
            <MiningField name="Windy" invalidValueTreatment="asIs"/>
            <MiningField name="Play Golf" invalidValueTreatment="asIs" usageType="target"/>
          </MiningSchema>
          <BayesInputs>
            <BayesInput fieldName="Outlook">
              <PairCounts value="Rainy">
                <TargetValueCounts>
                  <TargetValueCount value="No" count="3.0"/>
                  <TargetValueCount value="Yes" count="2.0"/>
                </TargetValueCounts>
              </PairCounts>
              <PairCounts value="Overcast">
                <TargetValueCounts>
                  <TargetValueCount value="No" count="0.0"/>
                  <TargetValueCount value="Yes" count="4.0"/>
                </TargetValueCounts>
              </PairCounts>
              <PairCounts value="Sunny">
                <TargetValueCounts>
                  <TargetValueCount value="No" count="2.0"/>
                  <TargetValueCount value="Yes" count="3.0"/>
                </TargetValueCounts>
              </PairCounts>
            </BayesInput>
            <BayesInput fieldName="Temp">
              <PairCounts value="Hot">
                <TargetValueCounts>
                  <TargetValueCount value="No" count="2.0"/>
                  <TargetValueCount value="Yes" count="2.0"/>
                </TargetValueCounts>
              </PairCounts>
              <PairCounts value="Mild">
                <TargetValueCounts>
                  <TargetValueCount value="No" count="2.0"/>
                  <TargetValueCount value="Yes" count="4.0"/>
                </TargetValueCounts>
              </PairCounts>
              <PairCounts value="Cool">
                <TargetValueCounts>
                  <TargetValueCount value="No" count="1.0"/>
                  <TargetValueCount value="Yes" count="3.0"/>
                </TargetValueCounts>
              </PairCounts>
            </BayesInput>
            <BayesInput fieldName="Humidity">
              <PairCounts value="High">
                <TargetValueCounts>
                  <TargetValueCount value="No" count="4.0"/>
                  <TargetValueCount value="Yes" count="3.0"/>
                </TargetValueCounts>
              </PairCounts>
              <PairCounts value="Normal">
                <TargetValueCounts>
                  <TargetValueCount value="No" count="1.0"/>
                  <TargetValueCount value="Yes" count="6.0"/>
                </TargetValueCounts>
              </PairCounts>
            </BayesInput>
            <BayesInput fieldName="Windy">
              <PairCounts value="false">
                <TargetValueCounts>
                  <TargetValueCount value="No" count="2.0"/>
                  <TargetValueCount value="Yes" count="6.0"/>
                </TargetValueCounts>
              </PairCounts>
              <PairCounts value="true">
                <TargetValueCounts>
                  <TargetValueCount value="No" count="3.0"/>
                  <TargetValueCount value="Yes" count="3.0"/>
                </TargetValueCounts>
              </PairCounts>
            </BayesInput>
          </BayesInputs>
          <BayesOutput fieldName="Play Golf">
            <TargetValueCounts>
              <TargetValueCount value="No" count="5.0"/>
              <TargetValueCount value="Yes" count="9.0"/>
            </TargetValueCounts>
          </BayesOutput>
        </NaiveBayesModel>
      </PMML>""".stripMargin


    val is = new ByteArrayInputStream(example.toString.getBytes)
    val source = ImportFilter.apply(new InputSource(is))
    val model: PMML = JAXBUtil.unmarshalPMML(source)

    val evaluator = ModelEvaluatorFactory.newInstance().newModelEvaluator(model).asInstanceOf[Evaluator]

    evaluator.verify()


    import scala.collection.JavaConversions._
    val args = new util.LinkedHashMap[FieldName, FieldValue]()

    val activeFields: util.List[InputField] = evaluator.getInputFields()

    println("Valid values for Outlook are Rainy or Overcast or Sunny")
    println("Valid values for Temp are Hot or Mild or Cool")
    println("Valid values for Humidity are High or Normal")
    println("Valid values for Windy are false or true")

    activeFields.foreach({
      anActiveField => {
        val anActiveFieldName: String = anActiveField.toString()
        var name = anActiveField.asInstanceOf[InputField].getName.toString

        println(s"Enter the value for the following field: ${name}")
        val fieldInput: String = scala.io.StdIn.readLine()

        val fieldName = new FieldName(name)
        args.put(fieldName, anActiveField.prepare(fieldInput))
      }
    })


    val results = evaluator.evaluate(args)
    println(results)

  }

}
