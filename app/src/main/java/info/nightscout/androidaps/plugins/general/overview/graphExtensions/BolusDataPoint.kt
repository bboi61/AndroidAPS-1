package info.nightscout.androidaps.plugins.general.overview.graphExtensions

import android.content.Context
import android.graphics.Color
import info.nightscout.androidaps.core.R
import info.nightscout.androidaps.database.entities.Bolus
import info.nightscout.androidaps.interfaces.ActivePlugin
import info.nightscout.androidaps.utils.DecimalFormatter
import info.nightscout.androidaps.utils.DefaultValueHelper
import info.nightscout.androidaps.utils.resources.ResourceHelper
import javax.inject.Inject

class BolusDataPoint @Inject constructor(
    val data: Bolus,
    private val resourceHelper: ResourceHelper,
    private val activePlugin: ActivePlugin,
    private val defaultValueHelper: DefaultValueHelper
) : DataPointWithLabelInterface {

    private var yValue = 0.0

    override fun getX(): Double = data.timestamp.toDouble()
    override fun getY(): Double = if (data.type == Bolus.Type.SMB) defaultValueHelper.determineLowLine() else yValue
    override fun getLabel(): String = DecimalFormatter.toPumpSupportedBolus(data.amount, activePlugin.activePump, resourceHelper)
    override fun getDuration(): Long = 0
    override fun getSize(): Float = 2f

    override fun getShape(): PointsWithLabelGraphSeries.Shape =
        if (data.type == Bolus.Type.SMB) PointsWithLabelGraphSeries.Shape.SMB
        else PointsWithLabelGraphSeries.Shape.BOLUS

    override fun getColor(context: Context): Int =
        if (data.type == Bolus.Type.SMB) resourceHelper.getAttributeColor(context, R.attr.smbColor)
        else if (data.isValid) resourceHelper.getAttributeColor(context, R.attr.colorAccent)
        else resourceHelper.getAttributeColor(null, R.attr.statuslightAlarm)

    override fun setY(y: Double) {
        yValue = y
    }
}