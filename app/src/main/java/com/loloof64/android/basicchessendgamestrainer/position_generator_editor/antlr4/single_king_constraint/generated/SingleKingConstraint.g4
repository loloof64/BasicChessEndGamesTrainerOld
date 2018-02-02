grammar SingleKingConstraint;

singleKingConstraint: variableAssign* 'return' booleanExpr ';';

variableAssign:   ID ':=' numericExpr ';' #numericAssign
                | ID ':=' booleanExpr ';' #booleanAssign
                ;

booleanExpr: '(' booleanExpr ')'                                #parenthesisBooleanExpr
            | ID                                                #booleanVariable
            | numericExpr 'in' rangeValue                       #rangeCheck
            | numericExpr op=('<'|'>'|'<='|'>=') numericExpr    #numericRelational
            | numericExpr op=('='|'<>') numericExpr             #numericEquality
            | booleanExpr 'and' booleanExpr                     #andComparison
            | booleanExpr 'or' booleanExpr                      #orComparison
            ;

rangeValue: fileRange | rankRange;
fileRange: '[' fileConstant ',' fileConstant ']';
rankRange: '[' rankConstant ',' rankConstant ']';
fileConstant: 'FileA' | 'FileB' | 'FileC' | 'FileD' |
              'FileE' | 'FileF' | 'FileG' | 'FileH';
rankConstant: 'Rank1' | 'Rank2' | 'Rank3' | 'Rank4' |
              'Rank5' | 'Rank6' | 'Rank7' | 'Rank8';

numericExpr: '(' numericExpr ')'                      #parenthesisNumericExpr
              | 'abs(' numericExpr ')'                #absoluteNumericExpr
              | NumericLitteral                       #litteralNumericExpr
              | ID                                    #numericVariable
              | fileConstant                          #fileConstantNumericExpr
              | rankConstant                          #rankConstantNumericExpr
              | numericExpr op=('+'|'-') numericExpr  #plusMinusNumericExpr
              ;

NumericLitteral: [1-9][0-9]*;
ID: [a-zA-Z][a-zA-Z0-9_]*;

WS : [ \t\r\n]+ -> skip;
