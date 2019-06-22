# Linkify

Use this module (or take a hint from it) if you would need _linkify_ capabilities. Do not
use `TextView.setAutolinkMask` (or specify `autolink` in XML) because it will remove all 
existing links and keep only the ones it creates.

Please note that usage of this plugin introduces significant performance drop due to not
optimal implementation of underlying `android.text.util.Linkify`. If you have any ideas of how
to improve this - PRs are welcome!