/*
 * Basic Chess Endgames : generates a position of the endgame you want, then play it against computer.
    Copyright (C) 2017-2018  Laurent Bernabe <laurent.bernabe@gmail.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

grammar SingleKingConstraint;

singleKingConstraint: variableAssign* 'return' booleanExpr ';';

variableAssign:   ID ':=' numericExpr ';' #numericAssign
                | ID ':=' booleanExpr ';' #booleanAssign
                ;

booleanExpr: '(' booleanExpr ')'                                #parenthesisBooleanExpr
            | ID                                                #booleanVariable
            | numericExpr op=('<'|'>'|'<='|'>=') numericExpr    #numericRelational
            | numericExpr op=('='|'<>') numericExpr             #numericEquality
            | booleanExpr 'and' booleanExpr                     #andComparison
            | booleanExpr 'or' booleanExpr                      #orComparison
            ;

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
