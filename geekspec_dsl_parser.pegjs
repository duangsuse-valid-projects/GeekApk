{
  let filterIsNotArray = (xs => xs.filter(x => !Array.isArray(x)));
  let isNotUndef = (s => s != 'undefined');
  let head = xs => xs[0]
}

GeekSpec = specs:(__ ApiSpec __)* { return specs.map(x => head(filterIsNotArray(x))); }

SingleLineComment
  = "#" !lineTerminator .*

entityName = letter+
interfaceName = letter+
urlPath "url letters" = (letter / [:/.{}?&=%])+
argName = letter+
possibleValue = letter+
fieldName = letter+

httpMethod
  = "GET" / "POST"
  / "PUT" / "DELETE"
  / "PATCH" / "OPTIONS"
  / "HEAD"

ApiSpec =
  name:interfaceName _ '(' _ a:Arg? as:(_ "," _ Arg)* _ ')'
  _ rtxd:('->' _ (ReturnType / Dict))? _ '=' _ url:urlPath {
    return {
      method: "GET",
      name: name.join(''),
      args: a == null ? [] : [a].concat(as.map(ra => ra[3])),
      return : isNotUndef(typeof rtxd) ? (rtxd == null ? null : rtxd[2]) : null,
      url: url.join('')
    };
  }
  /
  method:httpMethod '@' name:interfaceName _ '(' _ a:Arg? as:(_ "," _ Arg)* _ ')'
  _ rtxd:('->' _ (ReturnType / Dict))? _ '=' _ url:urlPath {
    return {
      method: method,
      name: name.join(''),
      args: a == null ? [] : [a].concat(as.map(ra => ra[3])),
      return : isNotUndef(typeof rtxd) ? (rtxd == null ? null : rtxd[2]) : null,
      url: url.join('')
    };
  }

Arg = name:argName _ q:('?' / "!"?) _ ov:OptionVals? {
  return {
    name: name.join(''),
    required: q != "?",
    options: ov
  };
}

OptionVals = '{' _ pv:possibleValue? pvs:(_ ',' _ possibleValue)* _ '}' {
  return pv == null ? [] : [pv.join('')].concat(pvs.map(rpv => rpv[3].join('')));
}

ReturnType
  = axo:("array" / "object") ':' a:Atom { return { type: axo, of: ((typeof a != 'string') ? a.join('') : a) }; }
  / Atom

Atom = "boolean" / "number"
     / "string" / "datetime"
     / "plain" / entityName

Dict = '[' _ di:DictItem? dis:(_ ',' _ DictItem)* _ ']' {
  return di == null ? [] : [di].concat(dis.map(rd => rd[3]))
}

DictItem
  = ReturnType
  / '$' name:fieldName ':' rt:ReturnType {
    return { name: name.join(''), type: rt };
}

_ "whitespace"
  = [ \t\n\r]*

__ "comment or whitespace"
  = SingleLineComment / _

letter "letter"
  = [A-Za-z0-9_\-:]

lineTerminator
  = [\n\r\u2028\u2029]
