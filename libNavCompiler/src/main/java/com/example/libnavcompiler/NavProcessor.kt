package com.example.libnavcompiler

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.example.libnavannotation.ActivityDestination
import com.example.libnavannotation.FragmentDestination
import com.google.auto.service.AutoService
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic
import javax.tools.StandardLocation
import kotlin.math.abs

@AutoService(Process::class)
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@SupportedAnnotationTypes(
    "com.example.libnavannotation.FragmentDestination",
    "com.example.libnavannotation.ActivityDestination"
)
class NavProcessor : AbstractProcessor() {

    private var messager: Messager? = null

    private var filer: Filer? = null

    private val OUTPUT_FILE_NAME = "destination.json"

    override fun init(processingEnv: ProcessingEnvironment?) {
        super.init(processingEnv)
        messager = processingEnv?.messager
        filer = processingEnv?.filer
    }

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment?
    ): Boolean {
        val fragmentElements =
            roundEnv?.getElementsAnnotatedWith(FragmentDestination::class.java)
        val activityElements =
            roundEnv?.getElementsAnnotatedWith(FragmentDestination::class.java)
        val destMap = HashMap<String, String>()
        if (fragmentElements?.isNotEmpty() == true || activityElements?.isNotEmpty() == true) {
            if (fragmentElements != null) {
                handleDestination(fragmentElements, FragmentDestination::class.java, destMap)
            }
            if (activityElements != null) {
                handleDestination(activityElements, ActivityDestination::class.java, destMap)
            }
        }
        // 生成文件 到 app/src/main/assets

        //app/src/main/assets
        var fos: FileOutputStream? = null
        var writer: OutputStreamWriter? = null
        try {
            //filer.createResource()意思是创建源文件
            //我们可以指定为class文件输出的地方，
            //StandardLocation.CLASS_OUTPUT：java文件生成class文件的位置，/app/build/intermediates/javac/debug/classes/目录下
            //StandardLocation.SOURCE_OUTPUT：java文件的位置，一般在/ppjoke/app/build/generated/source/apt/目录下
            //StandardLocation.CLASS_PATH 和 StandardLocation.SOURCE_PATH用的不多，指的了这个参数，就要指定生成文件的pkg包名了
            val resource = filer!!.createResource(
                StandardLocation.CLASS_OUTPUT,
                "",
                OUTPUT_FILE_NAME
            )
            val resourcePath = resource.toUri().path
            messager!!.printMessage(Diagnostic.Kind.NOTE, "resourcePath:$resourcePath")

            //由于我们想要把json文件生成在app/src/main/assets/目录下,所以这里可以对字符串做一个截取，
            //以此便能准确获取项目在每个电脑上的 /app/src/main/assets/的路径
            val appPath = resourcePath.substring(0, resourcePath.indexOf("app") + 4)
            val assetsPath = appPath + "src/main/assets/"
            val file = File(assetsPath)
            if (!file.exists()) {
                file.mkdirs()
            }

            //此处就是稳健的写入了
            val outPutFile = File(file, OUTPUT_FILE_NAME)
            if (outPutFile.exists()) {
                outPutFile.delete()
            }
            outPutFile.createNewFile()

            //利用fastjson把收集到的所有的页面信息 转换成JSON格式的。并输出到文件中
            val content = JSON.toJSONString(destMap)
            fos = FileOutputStream(outPutFile)
            writer = OutputStreamWriter(fos, "UTF-8")
            writer.write(content)
            writer.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (writer != null) {
                try {
                    writer.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (fos != null) {
                try {
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        return true
    }

    private fun handleDestination(
        elements: Set<Element?>,
        annotationClaz: Class<out Annotation?>,
        map: HashMap<String, String>
    ) {
        elements.forEach() { element ->
            val typeElement = element as TypeElement
            var pageUrl: String? = null
            val clazName: String? = typeElement.qualifiedName.toString()
            val id = abs(clazName.hashCode())
            var needLogin = false
            var asStarter = false
            var isFragment = false
            val annotation = typeElement.getAnnotation(annotationClaz)
            if (annotation is FragmentDestination) {
                pageUrl = annotation.pageUrl
                needLogin = annotation.needLogin
                asStarter = annotation.asStarter
                isFragment = true
            } else if (annotation is ActivityDestination) {
                pageUrl = annotation.pageUrl
                needLogin = annotation.needLogin
                asStarter = annotation.asStarter
                isFragment = false
            }

            // 填充hashMap
            if (map.containsKey(pageUrl)) {
                messager?.printMessage(
                    javax.tools.Diagnostic.Kind.ERROR,
                    "不同的页面不允许使用相同的pageUrl: $clazName"
                )
            } else {
                val jsonObject = JSONObject()
                jsonObject["id"] = id
                jsonObject["clazName"] = clazName
                jsonObject["needLogin"] = needLogin
                jsonObject["asStarter"] = asStarter
                jsonObject["clazName"] = clazName
                jsonObject["isFragment"] = isFragment

            }
        }
    }

}