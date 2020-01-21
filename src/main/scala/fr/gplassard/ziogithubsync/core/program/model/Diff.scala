package fr.gplassard.ziogithubsync.core.program.model

case class Diff[T](old: Option[T], expected: Option[T])
