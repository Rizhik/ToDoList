//find first appearanse of node in DOM by attribute's value
function findNode(head, attribute, value) 
{
	if(!(head instanceof Element))
	{
		return null;
	}

	if (head.getAttribute(attribute) == value) 
	{
		return head;
	}

	for (var i = 0; i < head.childNodes.length; i++) 
	{
		var result = findNode(head.childNodes[i],attribute, value);
		if (result != null) 
		{
			return result;
		}
	}
	
	return null;
}

//find first parent that has data-id
function getDataIdValue(target)
{
	var dataID =  "";

	var currentElement = target;
	while(currentElement!=null && (currentElement.getAttribute("data-id")==null || currentElement.getAttribute("data-id")==""))
	{
		currentElement = currentElement.parentElement;
	}

	if (currentElement==null)
	{
		return null;
	}

	return currentElement.getAttribute("data-id");
}


