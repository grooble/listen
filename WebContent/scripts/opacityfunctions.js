
function SetOpacity(elem, opacityAsInt)
{
	var opacityAsDecimal = opacityAsInt;
	
	if (opacityAsInt > 100)
		opacityAsInt = opacityAsDecimal = 100; 
	else if (opacityAsInt < 0)
		opacityAsInt = opacityAsDecimal = 0; 
	
	opacityAsDecimal /= 100;
	if (opacityAsInt < 1)
		opacityAsInt = 1; // IE7 bug, text smoothing cuts out if 0
	
	elem.style.opacity = opacityAsDecimal;
	elem.style.filter  = "alpha(opacity=" + opacityAsInt + ")";
}

function FadeOpacity(elemId, fromOpacity, toOpacity, time, fps)
{
	var steps = Math.ceil(fps * (time / 1000));
	var delta = (toOpacity - fromOpacity) / steps;
	
	FadeOpacityStep(elemId, 0, steps, fromOpacity, delta, (time / steps));
}

function FadeOpacityStep(elemId, stepNum, steps, fromOpacity, delta, timePerStep)
{
    SetOpacity(document.getElementById(elemId), Math.round(parseInt(fromOpacity) + (delta * stepNum)));

    if (stepNum < steps)
        setTimeout("FadeOpacityStep('" + elemId + "', " + (stepNum+1) + ", " + steps + ", " + fromOpacity + ", " + delta + ", " + timePerStep + ");", timePerStep);
}
